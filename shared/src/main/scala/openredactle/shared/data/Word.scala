package openredactle.shared.data

import upickle.default.ReadWriter

enum Word derives ReadWriter:
  case Known(str: String)
  case Unknown(length: Int)
  case Punctuation(char: Char)
