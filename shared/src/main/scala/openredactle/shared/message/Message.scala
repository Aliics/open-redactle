package openredactle.shared.message

import openredactle.shared.data.Word
import upickle.default.{*, given}

enum Message derives ReadWriter:
  // Inputs
  case RequestStart()
  case Join(gameId: String)

  // Outputs
  case GameState(gameId: String, playerCount: Int, words: Seq[Word])
  case PlayerJoined(position: Int)
  case PlayerLeft(position: Int)
  case Error(message: String)
