{-# LANGUAGE OverloadedStrings #-}

module Network.Wikipedia.Constants (wikipediaBaseRequest, wikipediaBaseAPIRequest) where

import Network.HTTP.Client

wikipediaBaseRequest :: Request
wikipediaBaseRequest = "https://en.wikipedia.org/wiki"

wikipediaBaseAPIRequest :: Request
wikipediaBaseAPIRequest = "https://en.wikipedia.org/w/api.php"
