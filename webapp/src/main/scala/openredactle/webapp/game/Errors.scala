package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

object Errors:
  def show(errorMessage: String): Unit =
    render(
      dom.document.getElementById("app"),
      div(
        fontFamily := "monospace",
        position := "absolute",
        top := "0",
        left := "0",
        right := "0",
        bottom := "0",
        backgroundColor := "black",
        color := "white",
        textAlign := "center",
        display := "flex",
        flexDirection := "column",
        justifyContent := "center",

        h1("Oh noes!"),
        p(errorMessage),

        a(
          href := "/",
          color := "lightblue",

          "Try again",
        ),
      ),
    )
