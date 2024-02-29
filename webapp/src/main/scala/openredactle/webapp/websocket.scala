package openredactle.webapp

import openredactle.shared.message
import openredactle.shared.message.InMessage.*
import openredactle.shared.message.OutMessage
import openredactle.shared.message.OutMessage.*
import openredactle.webapp.:=
import openredactle.webapp.game.*
import openredactle.webapp.settings.EmojiSelector
import openredactle.webapp.settings.vote.GiveUpBanner
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
    read[OutMessage](msg.data.asInstanceOf[String]) match
      case GameState(gameId, playerId, playerEmojis, articleData, guesses, hintsAvailable, secretPositions, giveUpVoteStatus) =>
        window.history.replaceState((), "", s"/game/$gameId") // Make url match nicely. :)

        Game.gameId := Some(gameId)
        Game.playerEmojis := playerEmojis
        StatusBar.hintsAvailable := hintsAvailable
        Article.articleData := articleData
        Article.secretPositions := secretPositions
        Guesses.guessedWords := guesses
      case NewGuess(emoji, guess, matchedCount, isHint) =>
        Guesses.guessedWords.update(_ :+ (emoji, guess, matchedCount, isHint))
      case GuessMatch(word, matches) =>
        Article.updateMatched(word, matches)
      case AlreadyGuessed(_, guess, isHint) =>
        Article.selectedGuess := Some(guess -> isHint)
      case PlayerJoined(playerId, emoji) =>
        Game.playerEmojis.update(_ + (playerId -> emoji))
      case PlayerLeft(playerId) =>
        Game.playerEmojis.update(_.removed(playerId))
      case GameWon(fullArticleData) =>
        Article.articleData := fullArticleData
      case HintUsed() =>
        StatusBar.hintsAvailable.update(_ - 1)
      case PlayerChangedEmoji(playerId, emoji) =>
        Game.playerEmojis.update(_.updated(playerId, emoji))
      case GiveUpVoteStatus(status) =>
        GiveUpBanner.status := status
      case GaveUp(fullArticleData) =>
        Article.articleData := fullArticleData
      case Error(errorMessage) =>
        Errors.show(errorMessage)

  ws
