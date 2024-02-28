package openredactle.webapp.game

import com.raquo.airstream.ownership.ManualOwner
import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.let
import openredactle.webapp.element.{RenderableElement, renderableElementToElement}
import openredactle.webapp.settings.SettingsPopup
import openredactle.webapp.{Colors, colored, layoutFlex, solidBorder}
import org.scalajs.dom.{MouseEvent, window}

import scala.scalajs.js.timers.setTimeout

object StatusBar extends RenderableElement:
  private val copyLinkButtonPromptText = "Copy"
  private val copyButtonDoneText = "Done"

  val hintsAvailable: Var[Int] = Var(0)
  let:
    given ManualOwner()
    hintsAvailable.signal.foreach: n =>
      Article.inHintMode.update(_ && n > 0)

  private val copyLinkButtonText = Var(copyLinkButtonPromptText)

  override lazy val renderElement: Element =
    div(
      borderTop := solidBorder(),
      layoutFlex(centered = true),
      justifyContent := "space-between",
      padding := "0 1rem",
      height := "3rem",

      span(
        fontSize := "16px",
        width := "5rem",

        children <-- Game.playerEmojis.signal.map(_.values.toSeq).map:
          case playerEmojis if playerEmojis.size > 3 =>
            playerEmojis.take(3).map(_.code).map(span(_)) :+
              span(s"(+${playerEmojis.size - 3})")
          case playerEmojis =>
            playerEmojis.map(_.code).map(span(_))
      ),

      div(
        layoutFlex(centered = true),
        columnGap := "0.5rem",

        child <-- Game.gameId.signal.map: gameId =>
          span(fontSize := "16px", gameId getOrElse ""),

        button(
          colored(),
          cls := "trim",
          width := "4rem",

          onClick --> copyShareUrl,
          child.text <-- copyLinkButtonText,
        ),
      ),

      div(
        child <-- Article.inHintMode.signal.map: inHintMode =>
          button(
            colored(bgColor = if inHintMode then Colors.actionHeld else Colors.action),
            cls := "trim",
            onClick --> (_ => Article.inHintMode.update(!_ && hintsAvailable.now() > 0)),
            child.text <-- hintsAvailable.signal.map(h => s"Hints: $h")
          ),
        button(
          colored(),
          cls := "trim",

          onClick.stopPropagation --> (e => SettingsPopup.toggle()),
          "Settings",

          SettingsPopup,
        ),
      ),
    )

  private def copyShareUrl(_e: MouseEvent): Unit =
    window.navigator.clipboard.writeText(window.location.href)

    copyLinkButtonText.set(copyButtonDoneText)
    setTimeout(500):
      copyLinkButtonText.set(copyLinkButtonPromptText)
