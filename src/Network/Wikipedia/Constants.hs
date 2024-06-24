{-# LANGUAGE OverloadedStrings #-}

module Network.Wikipedia.Constants
  ( wikipediaBaseURL,
    wikipediaBaseAPIRequest,
    wikipediaPageBatchSize,
  )
where

import Network.HTTP.Client

wikipediaBaseURL :: String
wikipediaBaseURL = "https://en.wikipedia.org/wiki"

wikipediaBaseAPIRequest :: Request
wikipediaBaseAPIRequest = "https://en.wikipedia.org/w/api.php"

wikipediaPageBatchSize :: Int
wikipediaPageBatchSize = 50
