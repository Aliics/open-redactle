package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{solidBorder, userSelect}
import org.scalajs.dom.window

object StatusBar:
  val playerCount: Var[Int] = Var(0)

  def renderElement: Element =
    div(
      borderTop := solidBorder(),
      display := "flex",
      justifyContent := "space-between",
      padding := "0.25rem",

      a(
        child.text <-- Game.gameId.signal.map(_ getOrElse ""),
        a(
          cursor := "pointer",
          color := "blue",
          marginLeft := "0.5rem",
          border := solidBorder("blue"),

          onClick --> { _ =>
            window.navigator.clipboard.writeText(Game.gameId.now().get)
          },

          span(
            fontSize := "12px",
            padding := "0 0.25rem",
            userSelect := "none",

            "Copy",
          ),
        ),
      ),

      span(child.text <-- playerCount.signal.map(c => s"Players: $c")),
    )
