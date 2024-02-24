package openredactle.webapp.settings

import com.raquo.airstream.ownership.ManualOwner
import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.let
import openredactle.webapp.element.{RenderableElement, toggleSwitch}
import openredactle.webapp.layoutFlex
import org.scalajs.dom.window.localStorage
import org.scalajs.dom.{document, window}

object ThemeSwitch extends RenderableElement:
  private val themeKey = "theme"
  private val darkThemeKey = "dark-theme"

  private val darkTheme = Var:
    ensureThemeStored()
    localStorage.getItem(darkThemeKey).toBoolean

  let:
    given ManualOwner()
    darkTheme.signal.foreach: darkTheme =>
      localStorage.setItem(darkThemeKey, darkTheme.toString)
      document.documentElement.setAttribute(themeKey, if darkTheme then "dark" else "light")

  def ensureThemeStored(): Boolean =
    Option(localStorage.getItem(darkThemeKey)).map(_.toBoolean).getOrElse:
      val isDark = window.matchMedia("(prefers-color-scheme: dark)").matches
      localStorage.setItem(darkThemeKey, isDark.toString)
      isDark

  override lazy val renderElement: Element =
    span(
      layoutFlex(direction = "row"),
      alignItems := "center",
      paddingLeft := "0.5rem",

      "Dark",
      toggleSwitch(darkTheme),
    )

