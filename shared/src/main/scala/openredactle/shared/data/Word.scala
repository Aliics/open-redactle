package openredactle.shared.data

import upickle.default.{*, given}

enum Word:
  case Known(str: String)
  case Unknown(length: Int)

object Word:
  given knownReadWriter: ReadWriter[Word.Known] = macroRW
  given unknownReadWriter: ReadWriter[Word.Unknown] = macroRW
  given readWriter: ReadWriter[Word] = macroRW
