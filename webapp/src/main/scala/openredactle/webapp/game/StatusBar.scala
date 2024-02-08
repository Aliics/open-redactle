package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{Colors, buttonStyle, solidBorder}
import org.scalajs.dom.{MouseEvent, window}

import scala.scalajs.js.timers.setTimeout

object StatusBar:
  private val copyLinkButtonPromptText = "Copy Link"
  private val copyButtonDoneText = "Copied!"

  val playerCount: Var[Int] = Var(0)
  val hintsAvailable: Var[Int] = Var(0)

  private val copyLinkButtonText = Var(copyLinkButtonPromptText)

  lazy val renderElement: Element =
    div(
      borderTop := solidBorder(),
      display := "flex",
      alignItems := "center",
      justifyContent := "space-between",
      padding := "0.1rem 1rem",

      span(
        fontSize := "14px",
        child.text <-- playerCount.signal.map(c => s"Players: $c"),
      ),

      div(
        child <-- Game.gameId.signal.map: gameId =>
          span(fontSize := "14px", gameId getOrElse ""),

        button(
          buttonStyle(),
          width := "5rem",

          onClick --> copyShareUrl,
          child.text <-- copyLinkButtonText,
        ),
      ),

      div(
        child <-- Article.inHintMode.signal.map: inHintMode =>
          button(
            buttonStyle(bgColor = if inHintMode then Colors.actionHeld else Colors.action),
            onClick --> (_ => Article.inHintMode.update(!_ && hintsAvailable.now() > 0)),
            child.text <-- hintsAvailable.signal.map(h => s"Hints: $h")
          ),
        button(
          buttonStyle(bgColor = Colors.danger),
          onClick --> (_ => window.location.href = "/"),
          "Leave Game",
        ),
      ),
    )

  private def copyShareUrl(_e: MouseEvent): Unit =
    val shareUrl = s"${window.location.host}/game/${Game.gameId.now().get}"
    window.navigator.clipboard.writeText(shareUrl)

    copyLinkButtonText.update(_ => copyButtonDoneText)
    setTimeout(500):
      copyLinkButtonText.update(_ => copyLinkButtonPromptText)
