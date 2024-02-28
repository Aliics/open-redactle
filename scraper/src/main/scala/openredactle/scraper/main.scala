package openredactle.scraper

import com.typesafe.scalalogging.Logger
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.stored.S3Storage
import openredactle.shared.wordsFromString
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

val minimumWatchers = 100
val minimumParagraphs = 20

@main def main(mode: Mode): Unit =
  val logger = Logger("scraper")

  logger.info(s"Running in $mode mode")

  val s3Storage = S3Storage()
  val indexData = s3Storage.fetchIndex()

  val articleInfos = mode match
    case Mode.Generate => fetchRandomArticles(amount = 500)
    case Mode.Update => refetchArticlesFromIndex(indexData.map(_._2))

  val articlesInfoContents = (
    for a@ArticleInfo(title, uri, watchers) <- articleInfos if watchers >= minimumWatchers yield
      val bodyContent = Jsoup.connect(uri.toString)
        .get()
        .getElementById("bodyContent")
        .selectXpath("""//div[contains(@class, "mw-content-ltr")]/*[self::h1 or self::h2 or self::h3 or self::p]""")
        .asScala.filter: el =>
          !Seq("External links", "References", "See also", "Literature cited", "Notes")
            .exists(el.text().startsWith(_))

      // Needs more than 10 paragraphs of at least tweet length lol.
      if bodyContent.count(_.text().length >= 280) >= minimumParagraphs then
        val articleData = bodyContentElementsToArticleData(bodyContent)

        logger.info(s"Article can be saved $a")
        val existingIndex = indexData.find(_._2 == uri.toString).map(_._1)
        val articleDataWithTitle = ArticleData.Title(wordsFromString(title)) +: articleData
        Some((a, articleDataWithTitle, existingIndex))
      else
        logger.info(s"Article did not contain enough content $a")
        None
    )
    .flatten
    .distinctBy(_._1) // Rare chance we get the same article using random.
    .zipWithIndex.map:
      case ((a, d, e), i) => (i, a, d, e)

  val infoContentsLen = articlesInfoContents.size
  logger.info(s"Writing $infoContentsLen items to index")

  if mode == Mode.Generate then
    s3Storage.updateIndex:
      indexData ++ articlesInfoContents.collect:
        case (i, ArticleInfo(_, uri, _), _, None) => (i + indexData.size) -> uri.toString

    logger.info(s"Writing $infoContentsLen items as s3 objects")
    for (i, _, articleData, existingIndex) <- articlesInfoContents do
      s3Storage.writeArticleData(existingIndex getOrElse (i + indexData.size), articleData)
  else
    s3Storage.updateIndex:
      articlesInfoContents.map:
        case (i, ArticleInfo(_, uri, _), _, _) => i -> uri.toString

    logger.info(s"Writing $infoContentsLen items as s3 objects")
    for (i, _, articleData, _) <- articlesInfoContents do
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
