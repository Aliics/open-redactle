package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.game.Game
import org.scalajs.dom

@main def main(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Game.renderElement,
  )
