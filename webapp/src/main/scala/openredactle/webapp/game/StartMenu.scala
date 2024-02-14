package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.*

object StartMenu:
  private val gameIdInput = Var("")

  lazy val renderElement: Element =
    div(
      layoutFlex("column"),
      alignSelf := "center",
      width := "22rem",
      overflow := "hidden",

      h1("Open Redactle"),
      div(
        layoutFlex("column"),
        alignItems := "center",
        padding := "2rem",
        border := solidBorder(),
        borderRadius := "0.5rem",

        renderJoinFormElement,
        renderDividingElement,
        renderStartElement,
      ),
      a(
        href := "https://github.com/Aliics/open-redactle",
        fontSize := "14px",
        marginTop := "1rem",
        padding := "0.5rem 0",
        border := solidBorder(),
        borderRadius := "0.5rem",

        "github.com/aliics/openredactle",
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
      button(colored(), "Join"),
    )

  private def renderStartElement =
    div(
      p("Start a new game"),
      button(
        colored(),
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
