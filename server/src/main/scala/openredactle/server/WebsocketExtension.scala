package openredactle.server

import com.typesafe.scalalogging.Logger
import openredactle.shared.message.OutMessage
import org.java_websocket.WebSocket
import upickle.default.write

extension (conn: WebSocket)(using logger: Logger)
  def send(message: OutMessage): Unit =
    try
      conn.send(write(message))
    catch
      case e => logger.warn(s"Could not write message to connection: $message")
