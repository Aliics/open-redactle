package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{Colors, centeredScreen}
import org.scalajs.dom

object Errors:
  def show(errorMessage: String): Unit =
    render(
      dom.document.getElementById("app"),
      div(
        centeredScreen,
        color := Colors.primary,
        backgroundColor := Colors.secondary,

        h1("Oh noes!"),
        p(errorMessage),
        a(
          href := "/",

          "Go home",
        ),
      ),
    )
