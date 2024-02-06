package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.*

object StartMenu:
  private val gameIdInput = Var("")

  def renderElement: Element =
    div(
      layoutFlex("column"),
      alignSelf := "center",
      width := "20rem",
      overflow := "hidden",

      h1("Open Redactle"),
      div(
        layoutFlex("column"),
        alignItems := "center",
        padding := "2rem",
        border := solidBorder(),

        renderJoinFormElement,
        renderDividingElement,
        renderStartElement,
      ),
    )

  private def renderJoinFormElement =
    form(
      onSubmit.preventDefault.mapTo(gameIdInput.now()) --> { id =>
        Game connect Some(id)
      },

      p("Join game"),
      input(
        required := true,
        placeholder := "Game ID",

        value <-- gameIdInput,
        onInput.mapToValue --> gameIdInput,
      ),
      button(buttonStyle(), "Join"),
    )

  private def renderStartElement =
    div(
      p("Start a new game"),
      button(
        buttonStyle(),
        width := "6rem",

        onClick --> (_ => Game.connect()),

        "Start",
      ),
    )
    
  private def renderDividingElement =
    div(
      borderLeft := solidBorder(),
      borderRight := solidBorder(),
      borderLeftWidth := "10rem",
      borderRightWidth := "10rem",
      height := "1px",
      margin := "2rem 0 1rem 0",
      padding := "0 0.5rem",

      div(transform := "translateY(-50%)", b("or")),
    )
