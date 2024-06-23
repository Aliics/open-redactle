{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}

module Data.Wikipedia.Article
  ( RandomInfos (..),
    RandomInfo (..),
    PageInfos (..),
    PageInfo (..),
  )
where

import Control.Applicative (empty)
import Data.Aeson
import GHC.Generics (Generic)

newtype RandomInfos = RandomInfos [RandomInfo] deriving (Show)

newtype PageInfos = PageInfos [PageInfo] deriving (Show)

data RandomInfo = RandomInfo
  { id :: Int,
    name :: String
  }
  deriving (Generic, Show)

data PageInfo = PageInfo
  { pageid :: Int,
    title :: String,
    watchers :: Int,
    length :: Int
  }
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

instance FromJSON RandomInfo

instance FromJSON PageInfo
