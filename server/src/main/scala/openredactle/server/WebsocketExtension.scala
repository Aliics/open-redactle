package openredactle.server

import com.typesafe.scalalogging.Logger
import openredactle.shared.message.Message
import org.java_websocket.WebSocket
import upickle.default.write

extension (conn: WebSocket)(using logger: Logger)
  def send(message: Message): Unit =
    try
      conn.send(write(message))
    catch
      case e => logger.warn(s"could not write message to connection: $message")

