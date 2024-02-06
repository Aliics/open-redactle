package openredactle.shared.message

import openredactle.shared.data.{ArticleData, Word}
import upickle.default.ReadWriter

enum Message derives ReadWriter:
  // Inputs
  case StartGame()
  case JoinGame(gameId: String)
  case AddGuess(gameId: String, guess: String)

  // Outputs
  case GameState(gameId: String, playerCount: Int, articleData: Seq[ArticleData], guesses: List[(String, Int)])
  case NewGuess(guess: String, matchedCount: Int)
  case GuessMatch(word: Word.Known, matches: Seq[(Int, Seq[Int])])
  case PlayerJoined()
  case PlayerLeft()
  case GameWon(fullArticleData: Seq[ArticleData])
  case Error(message: String)
