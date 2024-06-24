{-# LANGUAGE DeriveGeneric #-}

module Data.Wikipedia.Scraper (Article (..), ArticleData (..)) where

import Data.Aeson (ToJSON)
import Data.Text (Text)
import GHC.Generics (Generic)

newtype Article = Article [ArticleData] deriving (Generic, Show)

data ArticleData
  = Title Text
  | Heading Text
  | Paragraph Text
  | BulletPoints [Text]
  deriving (Generic, Show)

instance ToJSON Article

instance ToJSON ArticleData
