package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{buttonStyle, solidBorder}
import org.scalajs.dom.{MouseEvent, window}

import scala.scalajs.js.timers.setTimeout

object StatusBar:
  private val copyButtonPromptText = "Copy Link"
  private val copyButtonDoneText = "Link Copied!"

  val playerCount: Var[Int] = Var(0)

  private val copyButtonText = Var(copyButtonPromptText)

  def renderElement: Element =
    div(
      borderTop := solidBorder(),
      display := "flex",
      alignItems := "center",
      justifyContent := "space-between",
      padding := "0.1rem 1rem",

      a(
        child.text <-- Game.gameId.signal.map(_ getOrElse ""),
        button(
          buttonStyle(),

          onClick --> copyShareUrl,

          child.text <-- copyButtonText,
        ),
      ),

      span(child.text <-- playerCount.signal.map(c => s"Players: $c")),
    )

  private def copyShareUrl(_e: MouseEvent): Unit =
    val shareUrl = s"${window.location.host}/${Game.gameId.now().get}"
    window.navigator.clipboard.writeText(shareUrl)

    copyButtonText.update(_ => copyButtonDoneText)
    setTimeout(500):
      copyButtonText.update(_ => copyButtonPromptText)
