package openredactle.server.games

import openredactle.server.data.ImplicitLazyLogger
import org.java_websocket.WebSocket

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.*

object Games extends ImplicitLazyLogger:
  private val games = ConcurrentLinkedQueue[Game]()

  def create(): Game =
    val game = Game()
    games.add(game)
    game

  def withGame(id: String)(thunk: Game => Unit)(otherwise: => Unit): Unit =
    games.asScala.find(_.id == id) match
      case Some(game) => thunk(game)
      case None => otherwise

  def findPlayerGame(conn: WebSocket): Option[Game] =
    games.asScala.find(_.connectedPlayers.contains(conn))

  def runGameReaper()(using ExecutionContext): Future[Unit] =
    for
      _ <- Future(Thread.sleep(10_000))
      _ <- Future:
        val deadGames = games.asScala.filter: game =>
          val emptyGameTtl = game.lastDisconnectTime.get() + 5 * 60_000
          game.connectedPlayers.isEmpty && emptyGameTtl < Instant.now().toEpochMilli
        games removeAll deadGames.asJavaCollection
        logger.info(s"Removed ${deadGames.size} dead games")
      _ <- runGameReaper()
    yield ()
