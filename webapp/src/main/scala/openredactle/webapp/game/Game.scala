package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.message
import openredactle.shared.message.Message
import openredactle.webapp.*
import openredactle.webapp.element.{RenderableElement, renderableElementToElement}
import openredactle.webapp.startmenu.StartMenu
import org.scalajs.dom.{WebSocket, window}
import upickle.default.write

object Game extends RenderableElement:
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
      
  def requestHint(section: Int, num: Int): Unit =
    gameConnection.now().foreach: ws =>
      ws.send(write(Message.RequestHint(gameId.now().get, section, num)))

  lazy val renderElement: Element =
    div(
      layoutFlex("column"),
      height := "100vh",
      maxHeight := "100vh",
      backgroundColor := Colors.mainBackground,

      children <-- renderGameScreen
    )

  private val renderGameScreen: Signal[Seq[Element]] =
    gameConnection.signal.map:
      case Some(_) =>
        Seq(
          div(
            layoutFlex(),
            maxHeight := "calc(100% - 3rem)", // Sort of a hack for the status bar.
            flexGrow := "1",

            Article,
            Guesses,
          ),

          StatusBar,
        )
      case None =>
        Seq(
          div(
            centeredScreen,
            StartMenu,
          ),
        )
