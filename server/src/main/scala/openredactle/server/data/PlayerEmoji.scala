package openredactle.server.data

import openredactle.shared.data.Emoji
import org.java_websocket.WebSocket

import java.util.UUID

case class PlayerEmoji(
  id: UUID,
  emoji: Emoji,
) extends ToStringComparable[PlayerEmoji]
