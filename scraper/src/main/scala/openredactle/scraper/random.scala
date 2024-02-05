package openredactle.scraper

import sttp.client3.quick.*
import sttp.client3.{HttpClientSyncBackend, Response}
import upickle.default.{Reader, read}

private val baseApiUrl = uri"https://en.wikipedia.org/w/api.php?action=query&format=json&formatversion=2&uselang=en"
private val baseArticleUrl = uri"https://en.wikipedia.org/wiki"

private case class RandomResponse(query: RandomQuery)derives Reader
private case class RandomQuery(random: Seq[RandomInfo])derives Reader
private case class RandomInfo(id: Int, title: String)derives Reader

def fetchRandomArticles(amount: Int): Seq[ArticleInfo] =
  val backend = HttpClientSyncBackend()

  val response: Response[String] = quickRequest
    .get(uri"$baseApiUrl&list=random&rnnamespace=0&rnlimit=$amount")
    .send(backend)

  val RandomQuery(random) = read[RandomResponse](response.body).query

  random.map: info =>
    ArticleInfo(
      title = info.title,
      uri = uri"$baseArticleUrl?curid=${info.id}",
    )
