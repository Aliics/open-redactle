package openredactle.server

import com.typesafe.scalalogging.Logger
import openredactle.server.data.ImplicitLazyLogger
import openredactle.server.games.{Game, Games}
import openredactle.shared.message.{*, given}
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import upickle.default.read

import java.net.InetSocketAddress
import scala.jdk.CollectionConverters.*

object WsServer extends WebSocketServer(InetSocketAddress(8080)) with ImplicitLazyLogger:
  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit =
    logger.info(s"New connection: ${conn.getRemoteSocketAddress}")

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit =
    Games.findPlayerGame(conn) match
      case Some(game) =>
        game.disconnect(conn)
      case None =>
        logger.info("Disconnected player not in game")

  override def onMessage(conn: WebSocket, message: String): Unit =
    def connectToGame(game: Game): Unit =
      val playerCount = game.connect(conn)
      logger.info(s"Connected player to ${game.id}")
      conn.send(Message.GameState(
        gameId = game.id,
        playerCount = playerCount,
        articleData = game.articleData,
        guesses = game.guessedWords.asScala.map(g => g.word -> g.matchedCount).toList,
      ))

    val msg = read[Message](message)
    msg match
      case Message.StartGame() =>
        logger.info("Starting new game!")
        connectToGame(Games.create())
      case Message.JoinGame(gameId) =>
        Games.withGame(gameId)(connectToGame)(sendGameNotFoundError(conn, gameId))
      case Message.AddGuess(gameId, guess) =>
        Games.withGame(gameId)(_.addGuess(guess))(sendGameNotFoundError(conn, gameId))
      case _ =>
        logger.error(s"Invalid message $message")

  override def onError(conn: WebSocket, ex: Exception): Unit =
    logger.error(s"Error occurred: $ex")

  override def onStart(): Unit =
    logger.info("WS started")
    setConnectionLostTimeout(0)

  private def sendGameNotFoundError(conn: WebSocket, attemptedGameId: String): Unit =
    conn.send(Message.Error(s"Game not found with ID: $attemptedGameId"))
