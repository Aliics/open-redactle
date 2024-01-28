package openredactle.shared.message

import openredactle.shared.data.Word
import upickle.default.{*, given}

enum Message derives ReadWriter:
  // Inputs
  case StartGame()
  case JoinGame(gameId: String)
  case AddGuess(guess: Word)

  // Outputs
  case GameState(gameId: String, playerCount: Int, words: Seq[Word], guesses: List[String])
  case NewGuess(guess: Word)
  case PlayerJoined(position: Int)
  case PlayerLeft(position: Int)
  case Error(message: String)
