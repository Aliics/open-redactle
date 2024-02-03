package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.message.Message
import openredactle.shared.{let, message}
import org.scalajs.dom.{WebSocket, window}
import upickle.default.{read, write}

object Game:
  private val webSocket = let:
    val ws = WebSocket("ws://localhost:8080/", "ws")
    ws.onopen = _ =>
      window.location.pathname.stripPrefix("/") match
        case "" =>
          ws.send(write(Message.StartGame()))
        case gameId =>
          ws.send(write(Message.JoinGame(gameId)))

    ws.onmessage = msg =>
      val message = read[Message](msg.data.asInstanceOf[String])
      message match
        case Message.GameState(gameId, playerCount, articleData, guesses) =>
          window.history.replaceState((), "", gameId) // Make url match nicely. :)

          this.gameId.update(_ => Some(gameId))
          StatusBar.playerCount.update(_ => playerCount)
          Article.articleData.update(_ => articleData)
          Guesses.guessedWords.update(_ => guesses)
        case Message.NewGuess(guess, matchedCount) =>
          Guesses.guessedWords.update(_ :+ (guess, matchedCount))
        case Message.GuessMatch(word, matches) =>
          Article.updateMatched(word, matches)
        case Message.PlayerJoined() =>
          StatusBar.playerCount.update(_ + 1)
        case Message.PlayerLeft() =>
          StatusBar.playerCount.update(_ - 1)
        case Message.Error(errorMessage) =>
          Errors.show(errorMessage)
        case _ =>
          Errors.show(s"Unknown message: ${msg.data}")
    ws

  val gameId: Var[Option[String]] = Var(None)

  def addGuess(guess: String): Unit =
    webSocket.send(write(Message.AddGuess(gameId.now().get, guess)))

  def renderElement: Element =
    div(
      display := "flex",
      flexDirection := "column",
      height := "100vh",
      maxHeight := "100vh",

      div(
        display := "flex",
        maxHeight := "calc(100% - 2rem)", // Sort of a hack for the status bar.
        flexGrow := "1",

        Article.renderElement,
        Guesses.renderElement,
      ),

      StatusBar.renderElement,
    )
