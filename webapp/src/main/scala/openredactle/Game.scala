package openredactle

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object Game:

  def renderElement: Element =
    div(
      display := "flex",
      height := "100%",

      Article.renderElement,
      Guesses.renderElement,
    )
