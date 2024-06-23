module Network.Wikipedia.Constants (wikipediaUrl, wikipediaApiUrl) where

wikipediaUrl :: String
wikipediaUrl = "https://en.wikipedia.org/wiki"

wikipediaApiUrl :: String
wikipediaApiUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&formatversion=2&uselang=en"
