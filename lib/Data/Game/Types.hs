{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE FlexibleInstances #-}

module Data.Game.Types
  ( PlayerConn,
    GameState (..),
    GameEventChan,
    GameEvent (..),
    InputMessage (..),
    OutputMessage (..),
  )
where

import Control.Concurrent (Chan)
import Data.Aeson (FromJSON, ToJSON)
import Data.Text (Text)
import Data.UUID (UUID)
import GHC.Generics (Generic)
import qualified Network.WebSockets as WS

type PlayerConn = (UUID, WS.Connection)

data GameState = GameState
  { gameId :: UUID,
    gameEventQueue :: GameEventChan,
    gamePlayers :: [PlayerConn],
    gameGuessedWords :: [Text]
  }

type GameEventChan = Chan GameEvent

data GameEvent
  = ConnPlayer PlayerConn
  | DisConnPlayer PlayerConn
  | PlayerInput PlayerConn InputMessage

data InputMessage
  = MkGuess Text
  | UseHint Int
  deriving (Generic, Show)

data OutputMessage
  = PlayerJoined UUID
  | PlayerLeft UUID
  | GuessMade UUID Text
  deriving (Generic, Show)

instance FromJSON InputMessage

instance ToJSON InputMessage

instance FromJSON OutputMessage

instance ToJSON OutputMessage
