package openredactle.webapp.element

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{Colors, layoutFlex}

def toggleSwitch(checkedVar: Var[Boolean]) =
  span(
    layoutFlex(centered = true),

    input(
      `type` := "checkbox",
      backgroundColor <-- checkedVar.signal.map(if _ then Colors.actionHeld else Colors.tertiary),

      checked <-- checkedVar,
      onChange.mapToChecked --> checkedVar,
    ),
    span(
      position := "absolute",
      backgroundColor := Colors.primary,
      width := "1rem",
      height := "1rem",
      borderRadius := "1rem",
      cursor := "pointer",

      transform <-- checkedVar.signal.map(if _ then "translateX(1.5rem)" else "translateX(0.5rem)"),

      onClick --> (_ => checkedVar.update(!_)),
    ),
  )

