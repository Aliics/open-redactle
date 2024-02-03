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
