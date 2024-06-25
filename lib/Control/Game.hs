{-# LANGUAGE FlexibleInstances #-}

module Control.Game
  ( runNewGame,
    pushGameEvent,
  )
where

import Control.Concurrent
import Data.Game.Types
import Data.UUID.V4 (nextRandom)

newGameState :: IO GameState
newGameState = do
  newId <- nextRandom
  newEventQueue <- newMVar []
  pure
    GameState
      { gameId = newId,
        gameEventQueue = newEventQueue,
        gameGuessedWords = []
      }

runNewGame :: IO GameState
runNewGame = do
  state <- newGameState
  _ <- forkIO $ gameLoop state

  pure state

pushGameEvent :: GameEventQueue -> GameEvent -> IO ()
pushGameEvent m e = do
  modifyMVar_ m $ \es -> do
    pure $ es <> [e]

gameLoop :: GameState -> IO ()
gameLoop _ = pure ()
