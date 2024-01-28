package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.data.ArticleData
import openredactle.shared.data.ArticleData.*
import openredactle.shared.data.Word.*
import openredactle.webapp.renderElement

object Article:
  private val articleData = Var(
    Title(Seq(Unknown(5), Unknown(5))) ::
      Paragraph(Seq(Unknown(5), Unknown(5), Known("dolor"), Known("sit"), Known("amet"))) ::
      Nil
  )

  def renderElement: Element =
    div(
      height := "100%",
      flexGrow := 1,
      overflow := "scroll",
      fontFamily := "monospace",

      renderArticleData,
    )

  private def renderArticleData: Element =
    div:
      children <-- articleData.signal.splitByIndex(renderArticleData)

  private def renderArticleData(index: Int, initialArticle: ArticleData, articleDataSignal: Signal[ArticleData]): Element =
    val typ = initialArticle match
      case ArticleData.Title(words) => h1
      case ArticleData.Header(words) => h2
      case ArticleData.Paragraph(words) => p

    typ:
      children <-- articleDataSignal.map: data =>
        val words = data.words.map(_.renderElement)
        for (w <- words; spaced <- List(w, span(" "))) yield spaced
