package openredactle.scraper

import sttp.client3.quick.*
import sttp.client3.{HttpClientSyncBackend, Response}
import sttp.model.Uri
import upickle.default.{Reader, read}

private val baseApiUrl = uri"https://en.wikipedia.org/w/api.php?action=query&format=json&formatversion=2&uselang=en"
private val baseArticleUrl = uri"https://en.wikipedia.org/wiki"

private case class WikipediaResponse[Query](query: Query)derives Reader

private case class RandomQuery(random: Seq[RandomInfo])derives Reader
private case class RandomInfo(id: Int, title: String)derives Reader

private case class PagesQuery(pages: Seq[PageInfo])derives Reader
private case class PageInfo(pageid: Int, title: String)derives Reader

def fetchRandomArticles(amount: Int): Seq[ArticleInfo] =
  val RandomQuery(random) = queryWikipedia[RandomQuery](uri"$baseApiUrl&list=random&rnnamespace=0&rnlimit=$amount")

  random.map: info =>
    ArticleInfo(
      title = info.title,
      uri = uri"$baseArticleUrl?curid=${info.id}",
    )

def refetchArticlesFromIndex(indexData: Seq[(Int, String)]): Seq[ArticleInfo] =
  indexData.map(_._2)
    .grouped(50)
    .flatMap: uris =>
      val idsStr = uris.map { case s"$_?curid=$id" => id }.mkString("|")
      queryWikipedia[PagesQuery](uri"$baseApiUrl&prop=info&pageids=$idsStr").pages
    .toSeq
    .map: info =>
      ArticleInfo(
        title = info.title,
        uri = uri"$baseArticleUrl?curid=${info.pageid}",
      )

private def queryWikipedia[Query: Reader](uri: Uri): Query =
  val backend = HttpClientSyncBackend()

  val response: Response[String] = quickRequest
    .get(uri)
    .send(backend)

  read[WikipediaResponse[Query]](response.body).query
