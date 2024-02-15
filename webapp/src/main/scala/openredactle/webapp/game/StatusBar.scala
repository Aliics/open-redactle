package openredactle.webapp.game

import com.raquo.airstream.ownership.ManualOwner
import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.let
import openredactle.webapp.{Colors, colored, solidBorder}
import org.scalajs.dom.{MouseEvent, window}

import scala.scalajs.js.timers.setTimeout

object StatusBar:
  private val copyLinkButtonPromptText = "Copy Link"
  private val copyButtonDoneText = "Copied!"

  val playerCount: Var[Int] = Var(0)
  val hintsAvailable: Var[Int] = Var(0)
  let:
    given ManualOwner()
    hintsAvailable.signal.foreach: n =>
      Article.inHintMode.update(_ && n > 0)

  private val copyLinkButtonText = Var(copyLinkButtonPromptText)

  lazy val renderElement: Element =
    div(
      borderTop := solidBorder(),
      display := "flex",
      alignItems := "center",
      justifyContent := "space-between",
      padding := "0 1rem",
      height := "3rem",

      span(
        fontSize := "16px",
        child.text <-- playerCount.signal.map(c => s"Players: $c"),
      ),

      div(
        child <-- Game.gameId.signal.map: gameId =>
          span(fontSize := "16px", gameId getOrElse ""),

        button(
          colored(),
          width := "6rem",

          onClick --> copyShareUrl,
          child.text <-- copyLinkButtonText,
        ),
      ),

      div(
        child <-- Article.inHintMode.signal.map: inHintMode =>
          button(
            colored(bgColor = if inHintMode then Colors.actionHeld else Colors.action),
            onClick --> (_ => Article.inHintMode.update(!_ && hintsAvailable.now() > 0)),
            child.text <-- hintsAvailable.signal.map(h => s"Hints: $h")
          ),
        button(
          colored(bgColor = Colors.danger),
          onClick --> (_ => window.location.href = "/"),
          "Leave Game",
        ),
      ),
    )

  private def copyShareUrl(_e: MouseEvent): Unit =
    window.navigator.clipboard.writeText(window.location.href)

    copyLinkButtonText.update(_ => copyButtonDoneText)
    setTimeout(500):
      copyLinkButtonText.update(_ => copyLinkButtonPromptText)
