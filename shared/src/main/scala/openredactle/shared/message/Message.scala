package openredactle.shared.message

import openredactle.shared.data.{ArticleData, Word}
import upickle.default.ReadWriter

enum Message derives ReadWriter:
  // Inputs
  case StartGame()
  case JoinGame(gameId: String)
  case AddGuess(gameId: String, guess: String)

  // Outputs
  case GameState(gameId: String, playerCount: Int, articleData: Seq[ArticleData], guesses: List[String])
  case NewGuess(guess: String, matchedCount: Int)
  case GuessMatch(word: Word.Known, matches: Seq[(Int, Seq[Int])])
  case PlayerJoined(position: Int)
  case PlayerLeft(position: Int)
  case Error(message: String)
