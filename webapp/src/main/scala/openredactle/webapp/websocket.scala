package openredactle.webapp

import openredactle.shared.message
import openredactle.shared.message.Message
import openredactle.shared.message.Message.*
import openredactle.webapp.game.*
import openredactle.webapp.settings.EmojiSelector
import org.scalajs.dom.{WebSocket, window}
import upickle.default.{read, write}

import scala.scalajs.js

def connectWs(gameId: Option[String] = None): WebSocket =
  val ws = new WebSocket(Config.wsServerUrl)

  ws.onerror = _ =>
    Errors.show("Could not connect to server.")

  ws.onopen = _ =>
    gameId match
      case Some(gameId) =>
        ws.send(write(JoinGame(gameId, EmojiSelector.storedEmoji)))
      case None =>
        ws.send(write(StartGame(EmojiSelector.storedEmoji)))

  ws.onmessage = msg =>
    val message = read[Message](msg.data.asInstanceOf[String])
    message match
      case GameState(gameId, playerId, playerEmojis, articleData, guesses, hintsAvailable, secretPositions) =>
        window.history.replaceState((), "", s"/game/$gameId") // Make url match nicely. :)

        Game.gameId.set(Some(gameId))
        Game.playerEmojis.set(playerEmojis)
        StatusBar.hintsAvailable.set(hintsAvailable)
        Article.articleData.set(articleData)
        Article.secretPositions.set(secretPositions)
        Guesses.guessedWords.set(guesses)
      case NewGuess(emoji, guess, matchedCount, isHint) =>
        Guesses.guessedWords.update(_ :+ (emoji, guess, matchedCount, isHint))
      case GuessMatch(word, matches) =>
        Article.updateMatched(word, matches)
      case AlreadyGuessed(_, guess, isHint) =>
        Article.selectedGuess.set(Some(guess -> isHint))
      case PlayerJoined(playerId, emoji) =>
        Game.playerEmojis.update(_ + (playerId -> emoji))
      case PlayerLeft(playerId) =>
        Game.playerEmojis.update(_.removed(playerId))
      case GameWon(fullArticleData) =>
        Article.articleData.set(fullArticleData)
      case HintUsed() =>
        StatusBar.hintsAvailable.update(_ - 1)
      case PlayerChangedEmoji(playerId, emoji) =>
        Game.playerEmojis.update(_.updated(playerId, emoji))
      case Error(errorMessage) =>
        Errors.show(errorMessage)
      case _ =>
        Errors.show(s"Unknown message: ${msg.data}")

  ws
