package openredactle.webapp.settings

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.element.{RenderableElement, given}
import openredactle.webapp.{Colors, colored, rowGap}
import org.scalajs.dom.{document, window}

object SettingsPopup extends RenderableElement:
  private val showing = Var(false)

  document.body.onclick = _ => showing.set(false)

  def toggle(): Unit = showing.update(!_)

  override lazy val renderElement: Element =
    div(
      display <-- showing.signal.map(if _ then "block" else "none"),
      onClick.stopPropagation --> (_ => toggle()),

      div(
        colored(bgColor = Colors.mainBackground),
        rowGap := "0.5rem",
        cls := "card",
        padding := "0.5rem 0 0 0",
        overflow := "hidden",
        position := "absolute",
        transform := "translate(-3.5rem, -10rem)",
        width := "7rem",

        EmojiSelector,
        ThemeSwitch,

        onClick.stopPropagation --> (_ => ()),

        a(
          colored(bgColor = Colors.danger),
          cls := "popup-item",

          onClick --> (_ => window.location.href = "/"),

          "Leave",
        ),
      ),
    )
