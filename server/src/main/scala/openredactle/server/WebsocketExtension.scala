package openredactle.server

import openredactle.shared.message.Message
import org.java_websocket.WebSocket
import upickle.default.{*, given}

extension (conn: WebSocket)
  def send(message: Message): Unit =
    conn.send(write(message))
