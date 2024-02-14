package openredactle.webapp.game

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.data.ArticleData.*
import openredactle.shared.data.{ArticleData, Word}
import openredactle.webapp.game.article.renderWordElement
import openredactle.webapp.solidBorder

object Article:
  val articleData: Var[Seq[ArticleData]] = Var(Nil)
  val selectedGuess: Var[Option[(String, Boolean)]] = Var(None)
  val secretPositions: Var[Seq[(Int, Seq[Int])]] = Var(Nil)
  val inHintMode: Var[Boolean] = Var(false)

  lazy val renderElement: Element =
    div(
      height := "100%",
      flexGrow := 1,
      overflowY := "scroll",
      fontSize := "18px",
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
    div(
      cls <-- inHintMode.signal.map(if _ then "in-hint-mode" else ""),

      children <-- articleData.signal.splitByIndex(renderArticleData),
    )

  private def renderArticleData(section: Int, initialArticle: ArticleData, articleDataSignal: Signal[ArticleData]): Element =
    val inner = Seq(
      lineHeight := "1.5rem",

      children <-- articleDataSignal.map: articleData =>
        articleData.words.zipWithIndex.map: (word, num) =>
          renderWordElement(word, section, num),
    )

    initialArticle match
      case ArticleData.Title(words) => h1(borderBottom := solidBorder(), inner)
      case ArticleData.Header(words) => h2(borderBottom := solidBorder(), inner)
      case ArticleData.SubHeader(words) => h3(inner)
      case ArticleData.Paragraph(words) => p(inner)
