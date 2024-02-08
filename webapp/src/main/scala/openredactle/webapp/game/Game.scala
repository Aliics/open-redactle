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

  def connect(gameId: Option[String] = None): Unit =
    gameConnection.update(_ => Some(connectWs(gameId)))

  def addGuess(guess: String): Unit =
    gameConnection.now().foreach: ws =>
      ws.send(write(Message.AddGuess(gameId.now().get, guess)))

  lazy val renderElement: Element =
    div(
      layoutFlex("column"),
      height := "100vh",
      maxHeight := "100vh",
      backgroundColor := Colors.mainBackground,

      children <-- renderGameScreen
    )

  private val renderGameScreen =
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
            StartMenu.renderElement,
          ),
        )
