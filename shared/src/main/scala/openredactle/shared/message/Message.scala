package openredactle.shared.message

import openredactle.shared.data.{ArticleData, Emoji, Word}
import upickle.default.ReadWriter

enum InMessage derives ReadWriter:
  case StartGame(emoji: Emoji)
  case JoinGame(gameId: String, emoji: Emoji)
  case AddGuess(gameId: String, guess: String)
  case RequestHint(gameId: String, section: Int, position: Int)
  case ChangeEmoji(gameId: String, now: Emoji)

enum OutMessage derives ReadWriter:
  case GameState(
    gameId: String,
    playerId: String,
    playerEmojis: Map[String, Emoji],
    articleData: Seq[ArticleData],
    guesses: List[(String, String, Int, Boolean)],
    hintsAvailable: Int,
    secretPositions: Seq[(Int, Seq[Int])]
  )
  case NewGuess(playerId: String, guess: String, matchedCount: Int, isHint: Boolean)
  case GuessMatch(word: Word.Known, matches: Seq[(Int, Seq[Int])])
  case AlreadyGuessed(playerId: String, guess: String, isHint: Boolean)
  case PlayerJoined(playerId: String, emoji: Emoji)
  case PlayerLeft(playerId: String)
  case GameWon(fullArticleData: Seq[ArticleData])
  case HintUsed()
  case PlayerChangedEmoji(playerId: String, emoji: Emoji)
  case Error(message: String)
