package openredactle.scraper

import com.typesafe.scalalogging.Logger
import org.jsoup.Jsoup

import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  val logger = Logger("scraper")

  for a@ArticleInfo(title, uri) <- fetchRandomArticles(amount = 50) do
    val bodyContent = Jsoup.connect(uri.toString)
      .get()
      .getElementById("bodyContent")
      .selectXpath("""//div[contains(@class, "mw-content-ltr")]/*[self::h1 or self::h2 or self::h3 or self::p]""")
      .asScala.filter:
        _.getElementsByClass("mw-headline").isEmpty

    // Needs more than 10 paragraphs of at least tweet length lol.
    if bodyContent.count(_.text().length > 280) > 10 then
      logger.info(s"Writing article $a")
      bodyContent
        .map(_.text().replaceAll("\\[(.*)]", ""))
    else
      logger.info(s"Article did not contain enough content $a")
