package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.webapp.data.ArticleData
import openredactle.webapp.data.ArticleData.*
import openredactle.shared.data.Word.*

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
      children <-- articleData.signal.splitByIndex(ArticleData.renderArticleData)
