{-# LANGUAGE OverloadedStrings #-}

module Network.WikipediaSpec (spec) where

import Network.Wikipedia
import Test.Hspec

spec :: Spec
spec = describe "extractBatchIds" $ do
  it "should be empty with no ids" $ do
    extractBatchIds [] `shouldBe` ""
  it "should contain exactly one when only one id is present" $ do
    extractBatchIds [25] `shouldBe` "25"
  it "should contain exactly one when only one id is present" $ do
    extractBatchIds [25, 42, 102] `shouldBe` "25|42|102"
