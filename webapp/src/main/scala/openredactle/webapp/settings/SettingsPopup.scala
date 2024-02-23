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
        rowGap := "1rem",
        cls := "card",
        position := "absolute",
        transform := "translate(-5rem, -13.5rem)",
        width := "7rem",

        EmojiSelector,
        ThemeSwitch,

        onClick.stopPropagation --> (_ => ()),

        button(
          colored(bgColor = Colors.danger),
          onClick --> (_ => window.location.href = "/"),
          "Leave",
        ),
      ),
    )
