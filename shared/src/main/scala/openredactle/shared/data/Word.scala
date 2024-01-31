package openredactle.shared.data

import upickle.default.{*, given}

import scala.collection.mutable

enum Word derives ReadWriter:
  case Known(str: String)
  case Unknown(length: Int)
  case Punctuation(char: Char)
