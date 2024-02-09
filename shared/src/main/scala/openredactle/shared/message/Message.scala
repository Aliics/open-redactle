package openredactle.shared.message

import openredactle.shared.data.{ArticleData, Word}
import upickle.default.ReadWriter

enum Message derives ReadWriter:
  // Inputs
  case StartGame()
  case JoinGame(gameId: String)
  case AddGuess(gameId: String, guess: String)
  case RequestHint(gameId: String, section: Int, position: Int)

  // Outputs
  case GameState(
    gameId: String,
    playerCount: Int,
    articleData: Seq[ArticleData],
    guesses: List[(String, Int, Boolean)],
    hintsAvailable: Int,
    secretPositions: Seq[(Int, Seq[Int])]
  )
  case NewGuess(guess: String, matchedCount: Int, isHint: Boolean)
  case GuessMatch(word: Word.Known, matches: Seq[(Int, Seq[Int])])
  case PlayerJoined()
  case PlayerLeft()
  case GameWon(fullArticleData: Seq[ArticleData])
  case HintUsed()
  case Error(message: String)
