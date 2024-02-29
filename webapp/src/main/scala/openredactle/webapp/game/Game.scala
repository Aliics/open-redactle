package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.data.Emoji
import openredactle.shared.message
import openredactle.shared.message.InMessage
import openredactle.shared.message.InMessage.*
import openredactle.webapp.*
import openredactle.webapp.element.{RenderableElement, given}
import openredactle.webapp.settings.vote.GiveUpBanner
import org.scalajs.dom.{WebSocket, window}
import upickle.default.{Writer, write}

object Game extends RenderableElement:
  val gameId: Var[Option[String]] = Var(None)
  val playerId: Var[Option[String]] = Var(None)
  val playerEmojis: Var[Map[String, Emoji]] = Var(Map())

  private val gameConnection: Var[Option[WebSocket]] = Var:
    window.location.pathname.stripPrefix("/") match
      case s"game" => Some(connectWs())
      case s"game/$gameId" => Some(connectWs(Some(gameId)))
      case _ => None

  def connect(gameId: Option[String] = None): Unit =
    gameConnection.set(Some(connectWs(gameId)))

  def addGuess(guess: String): Unit =
    sendMessage(AddGuess.apply)(guess)

  def requestHint(section: Int, num: Int): Unit =
    sendMessage(RequestHint.apply)(section, num)

  def changeEmojiInGame(emoji: Emoji): Unit =
    sendMessage(ChangeEmoji.apply)(emoji)

  def startGiveUpVote(): Unit =
    sendMessage(StartGiveUpVote.apply)

  def addGiveUpVote(vote: Boolean): Unit =
    sendMessage(AddGiveUpVote.apply)(vote)

  override lazy val renderElement: Element =
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

            div(
              layoutFlex(direction = "column"),
              height := "100%",
              flexGrow := 1,

              Article,
              GiveUpBanner,
            ),
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

  private def sendMessage[I <: InMessage : Writer](apply: String => I): Unit =
    withGame: (gameId, gameConnection) =>
      gameConnection.send(write(apply(gameId)))

  private def sendMessage[I <: InMessage : Writer, A](apply: (String, A) => I)(arg: A): Unit =
    withGame: (gameId, gameConnection) =>
      gameConnection.send(write(apply(gameId, arg)))

  private def sendMessage[I <: InMessage : Writer, A0, A1](apply: (String, A0, A1) => I)(arg: A0, arg1: A1): Unit =
    withGame: (gameId, gameConnection) =>
      gameConnection.send(write(apply(gameId, arg, arg1)))

  private def withGame(thunk: (String, WebSocket) => Unit): Unit =
    if gameId.now().isEmpty || gameConnection.now().isEmpty then
      Errors.show("Not connected to game. Cannot perform action.")
    else
      thunk(gameId.now().get, gameConnection.now().get)
