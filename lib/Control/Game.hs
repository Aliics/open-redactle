{-# LANGUAGE FlexibleInstances #-}

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

newGameState :: IO GameState
newGameState = do
  newId <- nextRandom
  newEventQueue <- newChan
  pure
    GameState
      { gameId = newId,
        gameEventQueue = newEventQueue,
        gamePlayers = [],
        gameGuessedWords = []
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
process s@GameState {gamePlayers = players} (ConnPlayer c) = do
  broadcast players $ PlayerJoined (fst c)
  pure $ s {gamePlayers = players <> [c]}
process s@GameState {gamePlayers = players} (DisConnPlayer c) = do
  broadcast players $ PlayerLeft (fst c)
  pure $ s {gamePlayers = filter (\(pid, _) -> pid /= fst c) players}
process s@GameState {gamePlayers = players, gameGuessedWords = guesses} (PlayerInput (pid, _) (MkGuess g)) = do
  broadcast players $ GuessMade pid g
  pure $ s {gameGuessedWords = guesses <> [g]}
process s (PlayerInput _ (UseHint _)) = do
  --  broadcast players $ GuessMade g
  pure s

broadcast :: [PlayerConn] -> OutputMessage -> IO ()
broadcast [] _ = pure ()
broadcast (p : ps) m = do
  -- I don't actually care about our exceptions here. ConnectionExceptions are bound to happen.
  _ <- try (WS.sendTextData (snd p) $ encode m) :: IO (Either WS.ConnectionException ())
  broadcast ps m
