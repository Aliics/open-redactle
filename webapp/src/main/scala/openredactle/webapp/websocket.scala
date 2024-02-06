package openredactle.webapp

import openredactle.shared.{let, message}
import openredactle.shared.message.Message
import openredactle.webapp.game.*
import org.scalajs.dom.{WebSocket, window}
import upickle.default.{read, write}

import scala.scalajs.js

def connectWs(): WebSocket =
  val ws = WebSocket("ws://localhost:8081/", "ws")

  ws.onerror = _ =>
    Errors.show("Could not connect to server.")

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

        Game.gameId.update(_ => Some(gameId))
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
      case Message.GameWon(fullArticleData) =>
        Article.articleData.update(_ => fullArticleData)
      case Message.Error(errorMessage) =>
        Errors.show(errorMessage)
      case _ =>
        Errors.show(s"Unknown message: ${msg.data}")

  ws
