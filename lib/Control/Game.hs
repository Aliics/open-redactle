{-# LANGUAGE FlexibleInstances #-}
{-# LANGUAGE OverloadedStrings #-}

module Control.Game
  ( runNewGame,
  )
where

import Control.Concurrent
import Control.Exception (try)
import Data.Aeson (encode)
import Data.Game.Types
import Data.UUID.V4 (nextRandom)
import qualified Network.WebSockets as WS
import Network.Wikipedia.Article (scrapeArticleData)

newGameState :: IO GameState
newGameState = do
  newId <- nextRandom
  newEventQueue <- newChan
  (Just newArticle) <- scrapeArticleData 39381
  pure
    GameState
      { gameId = newId,
        gameEventQueue = newEventQueue,
        article = newArticle,
        playerConns = [],
        guessedWords = [],
        hintedWords = []
      }

runNewGame :: IO GameState
runNewGame = do
  state <- newGameState
  _ <- forkIO $ gameLoop state

  pure state

gameLoop :: GameState -> IO ()
gameLoop s = do
  e <- readChan $ gameEventQueue s
  procState <- process s e
  gameLoop procState

process :: GameState -> GameEvent -> IO GameState
process s@GameState {playerConns = pcs} (ConnPlayer c@(pid, _)) =
  withState
    s {playerConns = pcs <> [c]}
    $ broadcast (PlayerJoined pid)
process s@GameState {playerConns = pcs} (DisConnPlayer (pid, _)) =
  withState
    s {playerConns = filter ((== pid) . fst) pcs}
    $ broadcast (PlayerLeft pid)
process s@GameState {guessedWords = gs} (PlayerInput (pid, _) (MkGuess g)) =
  withState
    s {guessedWords = gs <> [(pid, g)]}
    $ broadcast (GuessMade pid g)
process gs@GameState {hintedWords = hws} (PlayerInput (pid, _) (UseHint _)) =
  withState
    gs {hintedWords = hws <> [(pid, "hint")]}
    $ broadcast (HintUsed pid "hint")

withState :: GameState -> (GameState -> IO ()) -> IO GameState
withState s f = f s >> pure s

broadcast :: OutputMessage -> GameState -> IO ()
broadcast _ GameState {playerConns = []} = pure ()
broadcast m gs@GameState {playerConns = (p : ps)} = do
  -- I don't actually care about our exceptions here. ConnectionExceptions are bound to happen.
  _ <- try (WS.sendTextData (snd p) $ encode m) :: IO (Either WS.ConnectionException ())
  broadcast m gs {playerConns = ps}
