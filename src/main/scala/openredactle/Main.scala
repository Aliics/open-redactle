package openredactle

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

@main def main(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Game.renderElement,
  )
