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
          this.gameId.update(_ => Some(gameId))
          Article.articleData.update(_ => articleData)
        case Message.NewGuess(guess, matchedCount) =>
          Guesses.guessedWords.update(_ :+ (guess, matchedCount))
        case Message.GuessMatch(word, matches) =>
          Article.updateMatched(word, matches)
        case Message.PlayerJoined(position) =>
          println("A player joined!")
        case Message.PlayerLeft(position) =>
          println("A player left!")
        case Message.Error(errorMessage) =>
          println(errorMessage)
        case _ =>
          println(s"Unknown message: ${msg.data}")
    ws

  private val gameId: Var[Option[String]] = Var(None)

  def addGuess(guess: String): Unit =
    webSocket.send(write(Message.AddGuess(gameId.now().get, guess)))

  def renderElement: Element =
    div(
      display := "flex",
      height := "100%",

      Article.renderElement,
      Guesses.renderElement,
    )
