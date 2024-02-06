package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{Colors, buttonStyle, solidBorder}
import org.scalajs.dom.{MouseEvent, window}

import scala.scalajs.js.timers.setTimeout

object StatusBar:
  private val copyLinkButtonPromptText = "Copy Link"
  private val copyIdButtonPromptText = "Copy ID"
  private val copyButtonDoneText = "Copied!"

  val playerCount: Var[Int] = Var(0)

  private val copyLinkButtonText = Var(copyLinkButtonPromptText)
  private val copyIdButtonText = Var(copyIdButtonPromptText)

  def renderElement: Element =
    div(
      borderTop := solidBorder(),
      display := "flex",
      alignItems := "center",
      justifyContent := "space-between",
      padding := "0.1rem 1rem",

      a(
        child.text <-- Game.gameId.signal.map(_ getOrElse ""),
        copyButton(
          copyIdButtonText,
          copyIdButtonPromptText,
          Game.gameId.now().get,
        ),
        copyButton(
          copyLinkButtonText,
          copyLinkButtonPromptText,
          s"${window.location.host}/game/${Game.gameId.now().get}",
        ),
        button(
          buttonStyle(bgColor = Colors.danger),
          onClick --> (_ => window.location.href = "/"),
          "Leave Game",
        ),
      ),

      span(child.text <-- playerCount.signal.map(c => s"Players: $c")),
    )

  private def copyButton(textVar: Var[String], promptText: String, shareUrl: => String) =
    button(
      buttonStyle(),
      width := "5rem",

      onClick --> copyShare(
        textVar,
        promptText,
        shareUrl,
      ),

      child.text <-- textVar,
    )

  private def copyShare(textVar: Var[String], text: String, shareUrl: => String)(_e: MouseEvent): Unit =
    window.navigator.clipboard.writeText(shareUrl)

    textVar.update(_ => copyButtonDoneText)
    setTimeout(500):
      textVar.update(_ => text)
