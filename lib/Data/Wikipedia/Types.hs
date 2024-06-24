{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}

module Data.Wikipedia.Types
  ( RandomInfos (..),
    RandomInfo (..),
    PageInfos (..),
    PageInfo (..),
    Article (..),
    ArticleData (..),
  )
where

import Control.Applicative (empty)
import Data.Aeson
import Data.Text (Text)
import GHC.Generics (Generic)

newtype RandomInfos = RandomInfos [RandomInfo] deriving (Show)

newtype PageInfos = PageInfos [PageInfo] deriving (Show)

data RandomInfo = RandomInfo
  { randomInfoId :: Int,
    randomInfoTitle :: String
  }
  deriving (Generic, Show)

data PageInfo = PageInfo
  { pageInfoId :: Int,
    pageInfoTitle :: String,
    pageInfoWatchers :: Maybe Int,
    pageInfoLength :: Int
  }
  deriving (Generic, Show)

newtype Article = Article [ArticleData] deriving (Generic, Show)

data ArticleData
  = Title Text
  | Heading Text
  | Paragraph Text
  | BulletPoints [Text]
  deriving (Generic, Show)

instance FromJSON RandomInfos where
  parseJSON (Object v) = do
    query <- v .: "query"
    random <- query .: "random"
    return $ RandomInfos random
  parseJSON _ = empty

instance FromJSON PageInfos where
  parseJSON (Object v) = do
    query <- v .: "query"
    pages <- query .: "pages"
    return $ PageInfos pages
  parseJSON _ = empty

instance FromJSON RandomInfo where
  parseJSON (Object v) = RandomInfo <$> v .: "id" <*> v .: "title"
  parseJSON _ = empty

instance FromJSON PageInfo where
  parseJSON (Object v) = PageInfo <$> v .: "pageid" <*> v .: "title" <*> v .:? "watchers" <*> v .: "length"
  parseJSON _ = empty

instance ToJSON Article

instance ToJSON ArticleData
