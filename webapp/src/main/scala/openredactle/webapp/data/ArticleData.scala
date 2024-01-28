package openredactle.webapp.data

import com.raquo.laminar.api.L.{*, given}

enum ArticleData(val words: Seq[Word]):
  case Title(override val words: Seq[Word]) extends ArticleData(words)
  case Header(override val words: Seq[Word]) extends ArticleData(words)
  case Paragraph(override val words: Seq[Word]) extends ArticleData(words)

object ArticleData:
  def renderArticleData(index: Int, initialArticle: ArticleData, articleDataSignal: Signal[ArticleData]): Element =
    val typ = initialArticle match
      case Title(words) => h1
      case Header(words) => h2
      case Paragraph(words) => p

    typ:
      children <-- articleDataSignal.map: data =>
        val words = data.words.map(_.renderElement)
        for (w <- words; spaced <- List(w, span(" "))) yield spaced
