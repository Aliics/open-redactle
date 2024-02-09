package openredactle.webapp

import openredactle.shared.{let, message}
import openredactle.shared.message.Message
import openredactle.webapp.game.*
import org.scalajs.dom.{WebSocket, window}
import upickle.default.{read, write}

import scala.scalajs.js

def connectWs(gameId: Option[String] = None): WebSocket =
  val ws = WebSocket(Settings.wsServerUrl, "ws")

  ws.onerror = _ =>
    Errors.show("Could not connect to server.")

  ws.onopen = _ =>
    gameId match
      case Some(gameId) =>
        ws.send(write(Message.JoinGame(gameId)))
      case None =>
        ws.send(write(Message.StartGame()))

  ws.onmessage = msg =>
    val message = read[Message](msg.data.asInstanceOf[String])
    message match
      case Message.GameState(gameId, playerCount, articleData, guesses, hintsAvailable, secretPositions) =>
        window.history.replaceState((), "", s"/game/$gameId") // Make url match nicely. :)

        Game.gameId.update(_ => Some(gameId))
        StatusBar.playerCount.update(_ => playerCount)
        StatusBar.hintsAvailable.update(_ => hintsAvailable)
        Article.articleData.update(_ => articleData)
        Article.secretPositions.update(_ => secretPositions)
        Guesses.guessedWords.update(_ => guesses)
      case Message.NewGuess(guess, matchedCount, isHint) =>
        Guesses.guessedWords.update(_ :+ (guess, matchedCount, isHint))
      case Message.GuessMatch(word, matches) =>
        Article.updateMatched(word, matches)
      case Message.PlayerJoined() =>
        StatusBar.playerCount.update(_ + 1)
      case Message.PlayerLeft() =>
        StatusBar.playerCount.update(_ - 1)
      case Message.GameWon(fullArticleData) =>
        Article.articleData.update(_ => fullArticleData)
      case Message.HintUsed() =>
        StatusBar.hintsAvailable.update(_ - 1)
      case Message.Error(errorMessage) =>
        Errors.show(errorMessage)
      case _ =>
        Errors.show(s"Unknown message: ${msg.data}")

  ws
