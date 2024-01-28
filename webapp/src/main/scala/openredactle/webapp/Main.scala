package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.codecs
import openredactle.webapp.Game
import org.scalajs.dom

@main def main(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Game.renderElement,
  )
