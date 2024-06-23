{-# LANGUAGE OverloadedStrings #-}

module Network.Wikipedia (queryNRandom) where

import Data.Aeson (eitherDecode)
import Data.ByteString.Char8 (pack)
import Data.Wikipedia.Article (RandomInfos (..))
import Network.HTTP.Client (queryString)
import qualified Network.HTTP.Client as HTTP
import Network.HTTP.Client.TLS (tlsManagerSettings)
import Network.Wikipedia.Constants (wikipediaBaseAPIRequest)

queryNRandom :: Int -> IO (Either String RandomInfos)
queryNRandom 0 = return $ Right $ RandomInfos []
queryNRandom l = do
  man <- clientManager
  resp <- HTTP.httpLbs (buildNRandomReq l) man
  return $ eitherDecode (HTTP.responseBody resp)

buildNRandomReq :: Int -> HTTP.Request
buildNRandomReq l =
  wikipediaBaseAPIRequest
    { queryString = "?action=query&format=json&formatversion=2&uselang=en&list=random&rnnamespace=0&rnlimit=" <> pack (show l)
    }

clientManager :: IO HTTP.Manager
clientManager = HTTP.newManager tlsManagerSettings
