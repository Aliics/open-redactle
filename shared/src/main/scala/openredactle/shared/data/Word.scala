package openredactle.shared.data

import upickle.default.ReadWriter

enum Word derives ReadWriter:
  case Punctuation(char: Char, hasSpace: Boolean)
  case Known(str: String, hasSpace: Boolean)
  case Unknown(length: Int, hasSpace: Boolean)
