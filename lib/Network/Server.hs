{-# LANGUAGE OverloadedStrings #-}

module Network.Server (runServer) where

import Control.Concurrent (MVar, modifyMVar_, newMVar, readMVar)
import Control.Game (runNewGame)
import Data.ByteString
import qualified Data.ByteString.Char8 as C
import Data.Game.Types (GameState (..))
import qualified Data.List as L
import Data.Maybe (fromMaybe)
import qualified Data.UUID as U
import Network.Server.Conn (handleConn)
import qualified Network.WebSockets as WS

runServer :: IO ()
runServer = do
  games <- newMVar [] :: IO (MVar [GameState])
  WS.runServer "127.0.0.1" 8080 $ app games

app :: MVar [GameState] -> WS.PendingConnection -> IO ()
app games pc = do
  conn <- WS.acceptRequest pc
  let path = WS.requestPath $ WS.pendingRequest pc

  state <- getGameEventQueue games $ C.unpack path

  case state of
    (Just s) -> do
      WS.withPingThread conn 30 (pure ()) (handleConn conn $ gameEventQueue s)
    Nothing -> do
      WS.sendClose conn ("Goodbye" :: ByteString)

getGameEventQueue :: MVar [GameState] -> String -> IO (Maybe GameState)
getGameEventQueue games "/" = do
  state <- runNewGame

  modifyMVar_ games $ \s -> do
    pure $ s <> [state]

  pure $ Just state
getGameEventQueue games ('/' : existingId) = do
  let existingUUID = fromMaybe U.nil $ U.fromString existingId
  existing <- readMVar games
  pure $ L.find (\s -> gameId s == existingUUID) existing
getGameEventQueue _ _ = pure Nothing
