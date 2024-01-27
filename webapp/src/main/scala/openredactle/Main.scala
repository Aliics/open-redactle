package openredactle

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.codecs
import org.scalajs.dom

@main def main(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Game.renderElement,
  )

val userSelect: StyleProp[String] = styleProp("user-select")
