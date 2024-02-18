package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.game.Game
import openredactle.webapp.startmenu.{EmojiSelector, ThemeSwitch}
import org.scalajs.dom

@main def main(): Unit =
  EmojiSelector.ensureRandomEmojiChosen()
  ThemeSwitch.ensureThemeStored()
  
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Game.renderElement,
  )
