package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.data.ArticleData.*
import openredactle.shared.data.{ArticleData, Word}
import openredactle.webapp.data.renderElement

object Article:
  val articleData: Var[Seq[ArticleData]] = Var(Nil)

  def renderElement: Element =
    div(
      height := "100%",
      flexGrow := 1,
      overflowY := "scroll",
      fontFamily := "monospace",
      padding := "0 1rem",

      renderArticleData,
    )

  def updateMatched(word: Word, matches: Seq[(Int, Seq[Int])]): Unit =
    articleData.update(_.zipWithIndex.map: (data, idx) =>
      matches.find(_._1 == idx) match
        case Some((_, wordIdxs)) =>
          data.copy(words = wordIdxs.foldLeft(wordIdxs -> data.words):
            case ((h :: t, w), _) => t -> w.updated(h, word)
          ._2)
        case None =>
          data
    )

  private def renderArticleData: Element =
    div:
      children <-- articleData.signal.splitByIndex(renderArticleData)

  private def renderArticleData(index: Int, initialArticle: ArticleData, articleDataSignal: Signal[ArticleData]): Element =
    val typ = initialArticle match
      case ArticleData.Title(words) => h1
      case ArticleData.Header(words) => h2
      case ArticleData.Paragraph(words) => p

    typ(
      lineHeight := "1.1rem",

      children <-- articleDataSignal.map:
        _.words.map(_.renderElement),
    )
