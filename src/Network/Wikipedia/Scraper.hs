{-# LANGUAGE OverloadedStrings #-}

module Network.Wikipedia.Scraper (scrapeArticleData) where

import Control.Applicative ((<|>))
import Control.Monad (guard)
import Data.Text as T
import Data.Wikipedia.Scraper
import Network.Wikipedia.Constants (wikipediaBaseURL)
import Text.HTML.Scalpel

scrapeArticleData :: Int -> IO (Maybe Article)
scrapeArticleData wikiId =
  scrapeURL (wikipediaBaseURL <> "?curid=" <> show wikiId) wikiData

wikiData :: Scraper Text Article
wikiData = do
  title <- text $ "h1" @: [hasClass "firstHeading"] // "span"
  let content = "div" @: [hasClass "mw-content-ltr", hasClass "mw-parser-output"]
  contents <- chroot content articleDatas
  return $ Article ([Title title] <> contents)

articleDatas :: Scraper Text [ArticleData]
articleDatas = chroots anySelector $ do
  paragraph <|> heading2 <|> heading3 <|> bulletPoints

paragraph :: Scraper Text ArticleData
paragraph = do
  h2 <- text "p"

  guard . not . T.null $ T.replace "\n" "" h2

  return $ Paragraph h2

heading2 :: Scraper Text ArticleData
heading2 = do
  h2 <- text "h2"
  return $ Heading h2

heading3 :: Scraper Text ArticleData
heading3 = do
  h3 <- text "h3"
  return $ Heading h3

bulletPoints :: Scraper Text ArticleData
bulletPoints = do
  lis <- texts $ "ul" // "li"

  guard . not $ Prelude.null lis

  return $ BulletPoints lis
