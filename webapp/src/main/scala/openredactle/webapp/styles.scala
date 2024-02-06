package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter

val userSelect: StyleProp[String] = styleProp("user-select")

def solidBorder(color: String = "black"): String =
  s"$color 1px solid"

def buttonStyle(fgColor: String = "white", bgColor: String = "black"): Seq[KeySetter.StyleSetter] =
  Seq(
    cursor := "pointer",
    color := fgColor,
    backgroundColor := bgColor,
    marginLeft := "0.5rem",
    border := solidBorder(fgColor),
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
  val highlightedWord = "rgb(172, 208, 240)"
