package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.message
import openredactle.shared.message.Message
import openredactle.webapp.*
import org.scalajs.dom.{WebSocket, window}
import upickle.default.write

object Game:
  val gameId: Var[Option[String]] = Var(None)

  private val gameConnection: Var[Option[WebSocket]] = Var:
    window.location.pathname.stripPrefix("/") match
      case s"game" => Some(connectWs())
      case s"game/$gameId" => Some(connectWs(Some(gameId)))
      case _ => None

  def addGuess(guess: String): Unit =
    gameConnection.now().foreach: ws =>
      ws.send(write(Message.AddGuess(gameId.now().get, guess)))

  def renderElement: Element =
    div(
      layoutFlex("column"),
      height := "100vh",
      maxHeight := "100vh",
      backgroundColor := "#fcfcfc",

      children <-- renderGameScreen
    )

  private val renderGameScreen: Signal[Seq[Element]] =
    gameConnection.signal.map:
      case Some(_) =>
        Seq(
          div(
            layoutFlex(),
            maxHeight := "calc(100% - 2rem)", // Sort of a hack for the status bar.
            flexGrow := "1",

            Article.renderElement,
            Guesses.renderElement,
          ),

          StatusBar.renderElement,
        )
      case None =>
        Seq(
          div(
            centeredScreen,
            renderMenuElement,
          ),
        )

  private def renderMenuElement: Element =
    val gameIdInput = Var("")

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

        form(
          onSubmit.preventDefault.mapTo(gameIdInput.now()) --> { id =>
            gameConnection.update(_ => Some(connectWs(Some(id))))
          },

          p("Join game"),
          input(
            required := true,
            placeholder := "Game ID",

            value <-- gameIdInput,
            onInput.mapToValue --> gameIdInput,
          ),
          button(buttonStyle(), "Join"),
        ),

        renderDividingElement,

        div(
          p("Start a new game"),
          button(
            buttonStyle(),
            width := "6rem",

            onClick --> (_ => gameConnection.update(_ => Some(connectWs(None)))),

            "Start",
          ),
        ),
      ),
    )

  private def renderDividingElement: Element =
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
