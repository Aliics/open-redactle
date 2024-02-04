package openredactle.scraper

import org.jsoup.Jsoup
import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  for ArticleData(title, uri) <- fetchRandomArticles(amount = 25) do
    val bodyContent = Jsoup.connect(uri.toString)
      .get()
      .getElementById("bodyContent")
      .selectXpath("""//div[contains(@class, "mw-content-ltr")]/*[self::h1 or self::h2 or self::h3 or self::p]""")

    if bodyContent.size() > 10 then
      println(s"Article: $title")
      println(bodyContent.asScala.map(_.text()).mkString("\n"))
