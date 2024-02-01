package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

object Errors:
  def show(errorMessage: String): Unit =
    render(
      dom.document.getElementById("app"),
      div(
        position := "absolute",
        width := "100%",
        height := "100%",
        top := "0",

        fontFamily := "monospace",
        color := "white",

        backgroundColor := "black",

        display := "flex",
        textAlign := "center",
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
