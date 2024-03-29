package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.*
import openredactle.webapp.element.{RenderableElement, given}
import openredactle.webapp.game.Game
import openredactle.webapp.settings.{EmojiSelector, ThemeSwitch}

object StartMenu extends RenderableElement:
  private val gameIdInput = Var("")

  override lazy val renderElement: Element =
    div(
      layoutFlex("column"),
      alignSelf := "center",
      width := "22rem",
      overflow := "hidden",

      h1("Open Redactle"),
      div(
        layoutFlex(direction = "row", centered = true),
        justifyContent := "space-evenly",
        cls := "card",

        EmojiSelector,
        ThemeSwitch,
      ),
      div(
        cls := "card",
        marginTop := "1rem",

        renderJoinFormElement,
        renderDividingElement,
        renderStartElement,
      ),
      a(
        href := "https://github.com/Aliics/open-redactle",
        cls := "card",
        fontSize := "14px",
        marginTop := "1rem",

        "github.com/aliics/openredactle",
      ),
    )

  private def renderJoinFormElement =
    form(
      onSubmit.preventDefault.mapTo(gameIdInput.now()) --> { id =>
        Game connect Some(id)
      },

      p("Join game"),
      div(
        layoutFlex(),
        columnGap := "0.5rem",

        input(
          required := true,
          placeholder := "Game ID",

          value <-- gameIdInput,
          onInput.mapToValue --> gameIdInput,
        ),
        button(colored(), "Join"),
      )
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
