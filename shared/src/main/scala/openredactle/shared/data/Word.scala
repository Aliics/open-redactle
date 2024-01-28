package openredactle.shared.data

import upickle.default.{*, given}

enum Word derives ReadWriter:
  case Known(str: String)
  case Unknown(length: Int)
