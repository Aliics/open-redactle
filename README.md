# open-redactle

Open Source Redactle written entirely in Scala. The frontend utilizes Laminar for reactivity and basic state management.
This is not meant to an exact clone of other redactle games, but its own thing.

# Development

The webapp has hot-reloading setup, but you need to run two commands to accomplish this, because there is a small
dependency on Vite.

```shell
sbt ~fastLinkJS &
npm run dev
```

I use IntelliJ, so I just run the server through my IDE. You could do it in sbt very easily though, and the command
would probably look something like this:

```shell
sbt server/run
```

# Scraping

Data is scraped using the wikipedia API, you can see the main flow for this
in [scraper module](./scraper/src/main/scala/openredactle/scraper/main.scala).

Not all articles are great for playing redactle on, so as a bare minimum I ensure that there are at least 20 good-sized
paragraphs present and 100 watchers.

The articles get their important paragraphs and headers pulled out, and I store each article as a single object keyed
as `articles/$i` in an S3 bucket. An `index` is kept up-to-date in the same bucket to help for random selection and
to make it possible to update already scraped articles.
