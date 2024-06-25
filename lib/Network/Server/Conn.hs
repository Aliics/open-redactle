{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}

module Network.Server.Conn (handleConn) where

import Control.Exception (finally)
import Control.Game (pushGameEvent)
import Control.Monad (forever)
import Data.Aeson (ToJSON, decode, encode)
import Data.ByteString.Lazy.Internal
import Data.Game.Types (GameEvent (..), GameEventQueue, InputMessage)
import Data.Text (Text)
import GHC.Generics (Generic)
import qualified Network.WebSockets as WS

handleConn :: WS.Connection -> GameEventQueue -> IO ()
handleConn conn eventQueue = finally loop disconnect
  where
    loop = forever $ do
      msg <- WS.receiveData conn :: IO ByteString
      case decode msg :: Maybe InputMessage of
        (Just input) -> do
          pushGameEvent eventQueue $ PlayerInput input
          WS.sendTextData conn $ encode Success
        Nothing -> WS.sendTextData conn (encode $ Error "Invalid input")
    disconnect =
      pushGameEvent eventQueue PlayerLeft

data ResponseMsg
  = Success
  | Error Text
  deriving (Generic, Show)

instance ToJSON ResponseMsg
