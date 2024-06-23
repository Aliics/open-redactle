{-# LANGUAGE OverloadedStrings #-}

module Network.Wikipedia.Constants
  ( wikipediaBaseRequest,
    wikipediaBaseAPIRequest,
    wikipediaPageBatchSize,
  )
where

import Network.HTTP.Client

wikipediaBaseRequest :: Request
wikipediaBaseRequest = "https://en.wikipedia.org/wiki"

wikipediaBaseAPIRequest :: Request
wikipediaBaseAPIRequest = "https://en.wikipedia.org/w/api.php"

wikipediaPageBatchSize :: Int
wikipediaPageBatchSize = 50
