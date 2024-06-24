{-# LANGUAGE OverloadedStrings #-}

module Network.Wikipedia (queryNRandom, fetchPagesInfo, extractBatchIds) where

import Data.Aeson (eitherDecode)
import Data.ByteString (ByteString, intercalate)
import Data.ByteString.Char8 (pack)
import Data.Wikipedia.Article (PageInfos (..), RandomInfos (..))
import Network.HTTP.Client (queryString)
import qualified Network.HTTP.Client as HTTP
import Network.HTTP.Client.TLS (tlsManagerSettings)
import Network.Wikipedia.Constants (wikipediaBaseAPIRequest, wikipediaPageBatchSize)

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

fetchPagesInfo :: [Int] -> IO (Either String PageInfos)
fetchPagesInfo [] = return $ Right $ PageInfos []
fetchPagesInfo ids = do
  man <- clientManager
  resp <- HTTP.httpLbs (buildPagesInfoReq ids) man
  nextPage <- fetchPagesInfo $ drop wikipediaPageBatchSize ids
  return $ eitherDecode (HTTP.responseBody resp) <> nextPage

buildPagesInfoReq :: [Int] -> HTTP.Request
buildPagesInfoReq ids =
  wikipediaBaseAPIRequest
    { queryString = "?action=query&format=json&formatversion=2&uselang=en&prop=info&inprop=watchers&pageids=" <> extractBatchIds ids
    }

extractBatchIds :: [Int] -> ByteString
extractBatchIds [] = ""
extractBatchIds ids =
  intercalate "|" batch
  where
    idBatch = take wikipediaPageBatchSize ids
    batch = pack . show <$> idBatch

clientManager :: IO HTTP.Manager
clientManager = HTTP.newManager tlsManagerSettings
