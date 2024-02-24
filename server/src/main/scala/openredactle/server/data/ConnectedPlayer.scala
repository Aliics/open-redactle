package openredactle.server.data

import org.java_websocket.WebSocket

import java.util.UUID

case class ConnectedPlayer(
  id: UUID,
  conn: WebSocket,
) extends ToStringComparable[ConnectedPlayer]
