package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter

val userSelect: StyleProp[String] = styleProp("user-select")

def solidBorder(color: String = Colors.black): String =
  s"solid 1px $color"

def buttonStyle(fgColor: String = Colors.white, bgColor: String = Colors.black): Seq[KeySetter.StyleSetter] =
  Seq(
    cursor := "pointer",
    color := fgColor,
    backgroundColor := bgColor,
    marginLeft := "0.5rem",
    border := "none",
    padding := "0.25rem 0.5rem",
  )

def centeredScreen: Seq[KeySetter.StyleSetter] =
  Seq(
    position := "absolute",
    width := "100%",
    height := "100%",
    top := "0",

    textAlign := "center",
    justifyContent := "center",
  ) ++ layoutFlex("column")

def layoutFlex(direction: String = "default"): Seq[KeySetter.StyleSetter] =
  Seq(
    display := "flex",
    flexDirection := direction,
  )

object Colors:
  val black = "rgb(50, 50, 50)"
  val white = "rgb(250, 250, 250)"
  val mainBackground = "rgb(250, 250, 250)"
  val highlightedWord = "rgb(172, 208, 240)"
  val correctWord = "rgb(223, 245, 255)"
  val danger = "rgb(238, 70, 70)"
  val action = "rgb(70, 70, 238)"
  val actionHeld = "rgb(30, 30, 248)"
  val secretWord = "rgb(200, 40, 30)"
  val hintBlock = "rgb(11, 74, 132)"
