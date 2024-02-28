package openredactle.server

import com.typesafe.scalalogging.Logger
import openredactle.server.data.{ConnectedPlayer, uuidToString}
import openredactle.server.games.{Game, Games}
import openredactle.server.metrics.{CloudWatchEmitter, Metric}
import openredactle.shared.data.Emoji
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.message.{InMessage, OutMessage}
import openredactle.shared.message.InMessage.*
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit
import upickle.default.read

import java.net.InetSocketAddress
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.*

class WsServer(games: Games)(using env: Env) extends WebSocketServer(InetSocketAddress(env.port)) with ImplicitLazyLogger:
  private val connCount = AtomicLong(0)
  override def onOpen(conn: WebSocket, handshake: ClientHandshake): Unit =
    connCount.incrementAndGet()
    logger.info(s"New connection: ${conn.getRemoteSocketAddress}")

  override def onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean): Unit =
    connCount.decrementAndGet()
    games.findPlayerGame(conn) match
      case Some(game) =>
        game.disconnectByConn(conn)
      case None =>
        logger.info("Disconnected player not in game")

  override def onMessage(conn: WebSocket, message: String): Unit =
    def connectToGame(emoji: Emoji, game: Game): Unit =
      val player = ConnectedPlayer(UUID.randomUUID, conn)
      game.connect(player, emoji)
      logger.info(s"Connected player to ${game.id}")

      conn.send(OutMessage.GameState(
        gameId = game.id,
        playerId = player.id,
        playerEmojis = game.playerEmojis.asScala.map(p => p.id.toString -> p.emoji).toMap,
        articleData = game.articleData,
        guesses = game.guessedWords.asScala.map(g => (g.player.id.toString, g.word, g.matchedCount, g.isHint)).toList,
        hintsAvailable = game.hintsAvailable.get(),
        secretPositions = game.secretPositions,
        giveUpVoteStatus = game.vote.currentStatus,
      ))

    given WebSocket = conn

    read[InMessage](message) match
      case StartGame(emoji) =>
        logger.info("Starting new game!")
        connectToGame(emoji, games.create())
      case JoinGame(gameId, emoji) =>
        games.withGame(gameId)(connectToGame(emoji, _))
      case AddGuess(gameId, guess) =>
        games.withGame(gameId)(_.addGuess(guess))
      case RequestHint(gameId, section, num) =>
        games.withGame(gameId)(_.requestHint(section, num))
      case ChangeEmoji(gameId, emoji) =>
        games.withGame(gameId)(_.changeEmoji(emoji))

  override def onError(conn: WebSocket, ex: Exception): Unit =
    logger.error(s"Error occurred: $ex")

  override def onStart(): Unit =
    logger.info("WS started")
    setReuseAddr(true)
    setConnectionLostTimeout(0)
    setConnectionLostTimeout(60)

  private def sendGameNotFoundError(attemptedGameId: String)(using conn: WebSocket): Unit =
    conn.send(OutMessage.Error(s"Game not found with ID: $attemptedGameId"))

  CloudWatchEmitter("open-redactle-server-websockets")(
    Metric("open-connections", StandardUnit.COUNT, () => connCount.doubleValue()),
  )
