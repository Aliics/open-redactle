package openredactle.server.data

case class PlayerGuess(
  player: ConnectedPlayer,
  word: String,
  matchedCount: Int,
  isHint: Boolean,
) extends ToStringComparable[PlayerGuess]
