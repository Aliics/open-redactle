package openredactle.shared.message

import openredactle.shared.data.{ArticleData, Emoji, Word}
import upickle.default.ReadWriter

enum Message derives ReadWriter:
  // Inputs
  case StartGame(emoji: Emoji)
  case JoinGame(emoji: Emoji, gameId: String)
  case AddGuess(gameId: String, guess: String)
  case RequestHint(gameId: String, section: Int, position: Int)

  // Outputs
  case GameState(
    gameId: String,
    playerCount: Int,
    articleData: Seq[ArticleData],
    guesses: List[(Emoji, String, Int, Boolean)],
    hintsAvailable: Int,
    secretPositions: Seq[(Int, Seq[Int])]
  )
  case NewGuess(emoji: Emoji, guess: String, matchedCount: Int, isHint: Boolean)
  case GuessMatch(word: Word.Known, matches: Seq[(Int, Seq[Int])])
  case AlreadyGuessed(emoji: Emoji, guess: String, isHint: Boolean)
  case PlayerJoined()
  case PlayerLeft()
  case GameWon(fullArticleData: Seq[ArticleData])
  case HintUsed()
  case Error(message: String)
