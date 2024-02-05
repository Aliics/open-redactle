package openredactle.scraper

import com.typesafe.scalalogging.Logger
import openredactle.shared.stored.S3Storage
import org.jsoup.Jsoup

import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  val logger = Logger("scraper")

  val articlesInfoContents = (
    for a@ArticleInfo(title, uri) <- fetchRandomArticles(amount = 50) yield
      val bodyContent = Jsoup.connect(uri.toString)
        .get()
        .getElementById("bodyContent")
        .selectXpath("""//div[contains(@class, "mw-content-ltr")]/*[self::h1 or self::h2 or self::h3 or self::p]""")
        .asScala.filter:
          _.getElementsByClass("mw-headline").isEmpty
        .map(_.text().replaceAll("\\[(.*)]", ""))

      // Needs more than 10 paragraphs of at least tweet length lol.
      if bodyContent.count(_.length > 280) > 10 then
        logger.info(s"Writing article $a")
        Some(a -> bodyContent)
      else
        logger.info(s"Article did not contain enough content $a")
        None
    ).flatten

  val s3Storage = S3Storage()
  val indexData = s3Storage.fetchIndex()
  
  s3Storage.updateIndex:
    indexData ++ articlesInfoContents.zipWithIndex.map:
      case ((ArticleInfo(_, uri), _), i) =>
        (i + indexData.length, uri.toString)
  
