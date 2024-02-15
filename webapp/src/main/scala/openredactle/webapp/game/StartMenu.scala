package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.data.Emoji
import openredactle.webapp.*

object StartMenu:
  private val gameIdInput = Var("")
  private lazy val emojiSelected = Var(storedEmojiString)

  lazy val renderElement: Element =
    div(
      layoutFlex("column"),
      alignSelf := "center",
      width := "22rem",
      overflow := "hidden",

      h1("Open Redactle"),
      div(
        cls := "card",

        label(forId := "emoji", "Choose an emoji"),
        emojiSelectElement,
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

  private def emojiSelectElement =
    val sel = select(
      idAttr := "emoji",
      fontSize := "22px",
      height := "3rem",
      width := "5rem",
      marginTop := "1rem",

      value <-- emojiSelected,

      Emoji.values.map: emoji =>
        option(
          value := emoji.toString,
          emoji.code,
        ),
    )

    sel.ref.onchange = _ =>
      emojiSelected.update(_ => sel.ref.value)
      storeEmoji(Emoji valueOf sel.ref.value)

    sel

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
