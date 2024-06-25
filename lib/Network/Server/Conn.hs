{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}

module Network.Server.Conn (handleConn) where

import Control.Concurrent (writeChan)
import Control.Exception (finally)
import Control.Monad (forever)
import Data.Aeson (ToJSON, decode, encode)
import Data.ByteString.Lazy.Internal
import Data.Game.Types (GameEvent (..), GameEventChan, InputMessage)
import Data.Text (Text)
import Data.UUID.V4 (nextRandom)
import GHC.Generics (Generic)
import qualified Network.WebSockets as WS

handleConn :: WS.Connection -> GameEventChan -> IO ()
handleConn conn eventQueue = do
  playerId <- nextRandom
  let playerConn = (playerId, conn)
  writeChan eventQueue $ ConnPlayer playerConn

  finally (loop playerConn) (disconnect playerConn)
  where
    loop playerConn = forever $ do
      msg <- WS.receiveData conn :: IO ByteString
      case decode msg :: Maybe InputMessage of
        (Just input) -> do
          writeChan eventQueue $ PlayerInput playerConn input
          WS.sendTextData conn $ encode Success
        Nothing -> WS.sendTextData conn (encode $ Error "Invalid input")

    disconnect playerConn =
      writeChan eventQueue $ DisConnPlayer playerConn

data ResponseMsg
  = Success
  | Error Text
  deriving (Generic, Show)

instance ToJSON ResponseMsg
