# open-redactle

Open Source Redactle written entirely in Scala. The frontend utilizes Laminar for reactivity and basic state management.
This is not meant to an exact clone of other redactle games, but its own thing.

# Development

To make life easier I wrote a [tiny script](./ops/run-local) to run everything locally. Just run that in your
terminal in the root directory, and things should hopefully just work.

If `server` doesn't start, that is probably due to a small bug with deallocating the port the websocket listens on.
Just change the port in [main.js](./main.js) and
in [WsServer](./server/src/main/scala/openredactle/server/WsServer.scala) temporarily to get it working again.

# Scraping

Data is scraped using the wikipedia API, you can see the main flow for this
in [scraper module](./scraper/src/main/scala/openredactle/scraper/main.scala).

Not all articles are great for playing redactle on, so as a bare minimum I ensure that there are at least 20 good-sized
paragraphs present and 100 watchers.

The articles get their important paragraphs and headers pulled out, and I store each article as a single object keyed
as `articles/$i` in an S3 bucket. An `index` is kept up-to-date in the same bucket to help for random selection and
to make it possible to update already scraped articles.
