package openredactle.server.games

import openredactle.server.metrics.{CloudWatchEmitter, Metric}
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.sumBy
import org.java_websocket.WebSocket
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import scala.concurrent.ExecutionContext.Implicits.global
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
    games.asScala.find(_.connectedPlayers.asScala.exists(_.conn == conn))

  def runGameReaper()(using ExecutionContext): Future[Unit] =
    for
      _ <- Future:
        val deadGames = games.asScala.filter: game =>
          val emptyGameTtl = game.lastConnectionTime.get() + 5 * 60_000
          game.connectedPlayers.isEmpty && emptyGameTtl < Instant.now().toEpochMilli
        games removeAll deadGames.asJavaCollection
        if deadGames.nonEmpty then
          logger.info(s"Removed ${deadGames.size} dead games")

      _ <- Future(Thread.sleep(10_000))
      _ <- runGameReaper()
    yield ()

  CloudWatchEmitter("open-redactle-server-games")(
    Metric("alive-games", StandardUnit.COUNT, () => games.size()),
    Metric("number-players", StandardUnit.COUNT, () => games.asScala.sumBy(_.connectedPlayers.size)),
  )
