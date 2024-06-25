{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE FlexibleInstances #-}

module Data.Game.Types
  ( GameState (..),
    GameEventQueue,
    GameEvent (..),
    InputMessage (..),
  )
where

import Control.Concurrent (MVar)
import Data.Aeson (FromJSON, ToJSON)
import Data.ByteString (ByteString)
import Data.Text (Text)
import Data.UUID (UUID)
import GHC.Generics (Generic)

data GameState = GameState
  { gameId :: UUID,
    gameEventQueue :: GameEventQueue,
    gameGuessedWords :: [ByteString]
  }
  deriving (Show)

type GameEventQueue = MVar [GameEvent]

data GameEvent
  = PlayerJoined
  | PlayerLeft
  | PlayerInput InputMessage
  deriving (Generic, Show)

data InputMessage
  = MkGuess Text
  | UseHint Int
  deriving (Generic, Show)

instance Show GameEventQueue where
  show _ = "MVar [GameEvent]"

instance FromJSON GameEvent

instance ToJSON GameEvent

instance FromJSON InputMessage

instance ToJSON InputMessage
