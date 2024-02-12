package openredactle.server

import com.typesafe.scalalogging.Logger
import openredactle.server.games.{Game, Games}
import openredactle.server.metrics.{CloudWatchEmitter, Metric}
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.message.*
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit
import upickle.default.read

import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.*

object WsServer extends WebSocketServer(InetSocketAddress(8080)) with ImplicitLazyLogger:
  private val connCount = AtomicLong(0)
  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit =
    connCount.incrementAndGet()
    logger.info(s"New connection: ${conn.getRemoteSocketAddress}")

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit =
    connCount.decrementAndGet()
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
        guesses = game.guessedWords.asScala.map(g => (g.word, g.matchedCount, g.isHint)).toList,
        hintsAvailable = game.hintsAvailable.get(),
        secretPositions = game.secretPositions,
      ))

    given WebSocket = conn

    val msg = read[Message](message)
    msg match
      case Message.StartGame() =>
        logger.info("Starting new game!")
        connectToGame(Games.create())
      case Message.JoinGame(gameId) =>
        Games.withGame(gameId)(connectToGame)(sendGameNotFoundError(gameId))
      case Message.AddGuess(gameId, guess) =>
        Games.withGame(gameId)(_.addGuess(guess))(sendGameNotFoundError(gameId))
      case Message.RequestHint(gameId, section, num) =>
        Games.withGame(gameId)(_.requestHint(section, num))(sendGameNotFoundError(gameId))
      case _ =>
        logger.error(s"Invalid message $message")

  override def onError(conn: WebSocket, ex: Exception): Unit =
    logger.error(s"Error occurred: $ex")

  override def onStart(): Unit =
    logger.info("WS started")
    setReuseAddr(true)
    setConnectionLostTimeout(0)
    setConnectionLostTimeout(60)

  private def sendGameNotFoundError(attemptedGameId: String)(using conn: WebSocket): Unit =
    conn.send(Message.Error(s"Game not found with ID: $attemptedGameId"))

  CloudWatchEmitter("open-redactle-server-websockets")(
    Metric("open-connections", StandardUnit.COUNT, () => connCount.doubleValue()),
  )
