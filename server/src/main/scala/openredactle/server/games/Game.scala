package openredactle.server.games

import openredactle.shared.message.Message
import openredactle.shared.data.Word.*
import openredactle.server.send
import openredactle.shared.data.Word
import org.java_websocket.WebSocket
import upickle.default.{*, given}

import scala.collection.mutable
import scala.math.random

class Game:
  val id: String =
    def rng = random * (words.length - 1)
    (0 to 2)
      .map(_ => words(rng.toInt))
      .mkString("-")

  private[games] val connectedPlayers: mutable.ListBuffer[WebSocket] = mutable.ListBuffer()

  def connect(conn: WebSocket): Int =
    connectedPlayers.synchronized:
      broadcast(Message.PlayerJoined(connectedPlayers.length))
      connectedPlayers += conn
      connectedPlayers.length

  def disconnect(conn: WebSocket): Int =
    connectedPlayers.synchronized:
      val found = connectedPlayers.indexOf(conn)
      broadcast(Message.PlayerLeft(found))
      connectedPlayers.remove(found)
      connectedPlayers.length

  private def broadcast(message: Message): Unit =
    connectedPlayers.synchronized:
      connectedPlayers.foreach(_.send(message))
      
  val words: mutable.ListBuffer[Word] = mutable.ListBuffer(
    Unknown(5), Unknown(5),
    Unknown(5), Unknown(5), Known("dolor"), Known("sit"), Known("amet"),
  )
