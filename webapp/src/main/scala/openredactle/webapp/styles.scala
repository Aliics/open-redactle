package openredactle.webapp

import com.raquo.laminar.api.*
import com.raquo.laminar.api.L.*

val userSelect: StyleProp[String] = styleProp("user-select")

def solidBorder(color: String = "black"): String =
  s"$color 1px solid"
