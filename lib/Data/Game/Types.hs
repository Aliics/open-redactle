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
import Data.Wikipedia.Types (Article)

type PlayerConn = (UUID, WS.Connection)

data GameState = GameState
  { gameId :: UUID,
    gameEventQueue :: GameEventChan,

    article :: Article,

    playerConns :: [PlayerConn],
    guessedWords :: [(UUID, Text)],
    hintedWords :: [(UUID, Text)]
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
  | HintUsed UUID Text
  | GameInfo UUID [UUID] [Text] [Text]
  deriving (Generic, Show)

instance FromJSON InputMessage

instance ToJSON InputMessage

instance FromJSON OutputMessage

instance ToJSON OutputMessage
