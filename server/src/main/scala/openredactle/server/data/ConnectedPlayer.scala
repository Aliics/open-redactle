package openredactle.server.data

import openredactle.shared.data.Emoji
import org.java_websocket.WebSocket

case class ConnectedPlayer(
  emoji: Emoji,
  conn: WebSocket,
) extends ToStringComparable[ConnectedPlayer]
