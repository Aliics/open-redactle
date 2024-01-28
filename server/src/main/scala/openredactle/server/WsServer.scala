package openredactle.server

import com.typesafe.scalalogging.Logger
import openredactle.server.games.Game
import openredactle.shared.message.{*, given}
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import upickle.default.{*, given}

import java.net.InetSocketAddress

object WsServer extends WebSocketServer(InetSocketAddress(8080)):
  private val logger = Logger(this.getClass)

  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit =
    logger.info(s"New connection: ${handshake.getResourceDescriptor}")

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit =
    games.findPlayerGame(conn) match
      case Some(game) =>
        game.disconnect(conn)
      case None =>
        logger.info("Disconnected player not in game")

  override def onMessage(conn: WebSocket, message: String): Unit =
    def connectToGame(game: Game): Unit =
      val playerCount = game.connect(conn)
      conn.send(Message.GameState(
        gameId = game.id,
        playerCount = playerCount,
        words = game.words.toSeq,
        guesses = List(),
      ))

    val msg = read[Message](message)
    msg match
      case Message.StartGame() =>
        logger.info("Starting new game!")
        connectToGame(games.create())
      case Message.JoinGame(gameId) =>
        games.findById(gameId) match
          case Some(game) => connectToGame(game)
          case None => conn.send(Message.Error("Game not found"))
      case Message.AddGuess(guess) =>
        // TODO: broadcast the new guess and add to game state
      case _ =>
        logger.error(s"Invalid message $message")

  override def onError(conn: WebSocket, ex: Exception): Unit =
    logger.error(s"Error occurred: $ex")

  override def onStart(): Unit =
    logger.info("WS started")
    setConnectionLostTimeout(0)
