package openredactle.scraper

import com.typesafe.scalalogging.Logger
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.stored.S3Storage
import openredactle.shared.wordsFromString
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  val logger = Logger("scraper")

  val s3Storage = S3Storage()
  val indexData = s3Storage.fetchIndex()

  val articlesInfoContents = (
    for a@ArticleInfo(title, uri) <- fetchRandomArticles(amount = 50) yield
      val bodyContent = Jsoup.connect(uri.toString)
        .get()
        .getElementById("bodyContent")
        .selectXpath("""//div[contains(@class, "mw-content-ltr")]/*[self::h1 or self::h2 or self::h3 or self::p]""")
        .asScala.filter:
          _.getElementsByClass("mw-headline").isEmpty

      // Needs more than 10 paragraphs of at least tweet length lol.
      if bodyContent.count(_.text().length > 280) > 10 then
        val articleData = bodyContentElementsToArticleData(bodyContent)

        logger.info(s"Article can be saved $a")
        Some(a -> (ArticleData.Title(wordsFromString(title)) +: articleData))
      else
        logger.info(s"Article did not contain enough content $a")
        None
    )
    .flatten
    .zipWithIndex.map:
      case ((a, d), i) => (i + indexData.length, a, d)

  logger.info(s"Writing ${articlesInfoContents.length} items to index")
  s3Storage.updateIndex:
    indexData ++ articlesInfoContents.map(a => a._1 -> a._2.uri.toString)

  logger.info(s"Writing ${articlesInfoContents.length} items as s3 objects")
  for (i, _, articleData) <- articlesInfoContents do
    s3Storage.writeArticleData(i, articleData)
end main

private def bodyContentElementsToArticleData(bodyContent: mutable.Buffer[Element]) =
  bodyContent.toSeq.map: element =>
    val dataType: Seq[Word] => ArticleData =
      element.tagName match
        case "h1" => ArticleData.Title.apply
        case "h2" => ArticleData.Header.apply
        case "h3" => ArticleData.SubHeader.apply
        case "p" => ArticleData.Paragraph.apply

    dataType(wordsFromString(element.text().replaceAll("\\[(.*)]", "")))
