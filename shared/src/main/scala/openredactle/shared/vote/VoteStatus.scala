package openredactle.shared.vote

import upickle.default.ReadWriter

enum VoteStatus derives ReadWriter:
  case InActive()
  case Active(votes: Int, needed: Int)
  case Success()
  case Failure()
