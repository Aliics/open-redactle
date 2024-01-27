package openredactle

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.ArticleData.*
import openredactle.Word.*
import org.scalajs.dom.HTMLParagraphElement

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
    div(
      children <-- articleData.signal.map: articleData =>
        articleData.map: data =>
          val typ = data match
            case Title(words) => h1
            case Header(words) => h2
            case Paragraph(words) => p

          typ:
            val words = data.words.map(_.element)
            for (w <- words; spaced <- List(w, span(" "))) yield spaced
    )
