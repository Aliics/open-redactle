package openredactle.server.games

import openredactle.server.data.{*, given}
import openredactle.server.games.vote.Voting
import openredactle.server.send
import openredactle.shared.data.Word.*
import openredactle.shared.data.{ArticleData, Emoji, Word}
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.message.OutMessage
import openredactle.shared.message.OutMessage.*
import openredactle.shared.stored.S3Storage
import openredactle.shared.vote.VoteStatus
import openredactle.shared.vote.VoteStatus.{Failure, Success}
import openredactle.shared.{data, let, random, roughEquals}
import org.java_websocket.WebSocket

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions

class Game(using ExecutionContext) extends ImplicitLazyLogger:
  val playerEmojis: ConcurrentLinkedQueue[PlayerEmoji] = ConcurrentLinkedQueue()
  val guessedWords: ConcurrentLinkedQueue[PlayerGuess] = ConcurrentLinkedQueue()
  val voting: Voting = Voting()

  val hintsAvailable: AtomicInteger = AtomicInteger(3)

  val id: String = (0 to 2).map(_ => randomWords.random).mkString("-")

  private[games] val connectedPlayers = ConcurrentLinkedQueue[ConnectedPlayer]()
  private[games] val lastConnectionTime = AtomicLong(Instant.now().toEpochMilli)
  private val gameWon = AtomicBoolean(false)
  private val gaveUp = AtomicBoolean(false)

  private val fullArticleData = let:
    val indexData = Game.s3Storage.fetchIndex()
    val r@(i, _) = indexData.random
    logger.info(s"Selected article: $r")
    Game.s3Storage.getArticleByIndex(i)

  def connect(connectedPlayer: ConnectedPlayer, emoji: Emoji): Unit =
    lastConnectionTime.set(Instant.now().toEpochMilli)

    broadcast(PlayerJoined(connectedPlayer.id, emoji))
    connectedPlayers.add(connectedPlayer)
    playerEmojis.add(PlayerEmoji(connectedPlayer.id, emoji))
    broadcastVoteStatus(voting.recalculateRequirements(playerIds))

  def disconnectByConn(conn: WebSocket): Unit =
    lastConnectionTime.set(Instant.now().toEpochMilli)

    val playerId = conn.id
    connectedPlayers.removeIf(_.id == playerId)
    playerEmojis.removeIf(_.id == playerId)
    broadcast(PlayerLeft(playerId))
    broadcastVoteStatus(voting.recalculateRequirements(playerIds))

  def addGuess(rawGuess: String, isHint: Boolean = false)(using conn: WebSocket): Unit =
    val guess = rawGuess.trim
    val isFreeWord = freeWords.exists(_ equalsIgnoreCase guess)
    val alreadyGuessed = guessedWords.asScala.find(g => roughEquals(g.word)(guess))
    if !guess.isBlank && !isFreeWord && alreadyGuessed.isEmpty then
      val (matches, matchedCount) = getMatchingGuessData(fullArticleData)(guess)

      guessedWords.add(PlayerGuess(conn, guess, matchedCount, isHint))
      logger.info(s"""${if isHint then "Hint" else "Guess"} in $id: "$guess"""")
      broadcast(NewGuess(conn.id, guess, matchedCount, isHint))

      if articleData.head.words.exists(_.isInstanceOf[Word.Unknown]) then
        matches.foreach: (word, matches) =>
          broadcast(GuessMatch(word, matches))
      else
        logger.info(s"""Game won $id with guess: "$guess"""")
        broadcast(GameWon(fullArticleData))
        gameWon set true

        // Ensure no weird voting can happen afterwards.
        // It just looks weird.
        voting.stopVoting()
        voting.lock()
    else if alreadyGuessed.isDefined then
      val PlayerGuess(_, word, _, isHint) = alreadyGuessed.get
      conn.send(AlreadyGuessed(conn.id, word, isHint))

  def requestHint(section: Int, num: Int)(using WebSocket): Unit =
    val avail = hintsAvailable.get()
    if avail > 0 && hintsAvailable.compareAndSet(avail, avail - 1) then
      fullArticleData.lift(section).flatMap(_.words.lift(num)) match
        case Some(Word.Known(str, _)) =>
          addGuess(str, isHint = true)
          broadcast(HintUsed())
        case _ =>
          logger.warn(s"Attempted to give hint at position: $section, $num")

  def changeEmoji(emoji: Emoji)(using conn: WebSocket): Unit =
    playerEmojis.asScala.find(_.id == conn.id) match
      case Some(PlayerEmoji(id, _)) =>
        playerEmojis.removeIf(_.id == id)
        playerEmojis.add(PlayerEmoji(id, emoji))
      case None =>
        logger.warn("Could not find PlayerEmoji")

    broadcast(PlayerChangedEmoji(conn.id, emoji))

  def startVoting(): Unit =
    broadcastVoteStatus(voting startVoting playerIds)

  def addVote(vote: Boolean)(using conn: WebSocket): Unit =
    if gaveUp.get() then logger.info(s"""Game "$id" has already given up""")
    else
      broadcastVoteStatus(voting.addPlayerVote(conn.id, vote)) match
        case Success() =>
          logger.info(s"""Game "$id" gave up""")
          voting.lock()
          gaveUp set true
          broadcast(GaveUp(fullArticleData))
        case Failure() =>
          Future:
            // Crudely wait a few seconds before broadcasting "InActive" again.
            Thread sleep 3_000L
            broadcastVoteStatus(voting.stopVoting())
        case s@_ =>
          logger.info(s"""Game "$id" voting status: $s""")

  def articleData: Seq[ArticleData] =
    if gameWon.get() || gaveUp.get() then fullArticleData
    else
      fullArticleData.map: articleData =>
        articleData.copy(words = articleData.words.collect:
          case p: Punctuation => p
          case known@Known(str, hasSpace) =>
            val matchedGuessedWords = guessedWords.asScala.filter(_.matchedCount > 0).map(_.word)
            if (freeWords ++ matchedGuessedWords).exists(roughEquals(_)(str)) then known
            else Unknown(str.length, hasSpace)
        )

  def secretPositions: Seq[(Int, Seq[Int])] =
    val secrets = fullArticleData.headOption.toSeq
      .flatMap(_.words)
      .collect:
        case Word.Known(str, _) => str

    fullArticleData.zipWithIndex.map: (articleData, i) =>
      val matched = articleData.words.zipWithIndex.collect:
        case (Word.Known(str, _), i) if secrets.exists(roughEquals(_)(str)) => i
      i -> matched

  private def playerIds = connectedPlayers.asScala.map(_.id).toSeq

  private def broadcast(message: OutMessage): Unit =
    connectedPlayers.asScala.foreach(_.conn.send(message))

  private def broadcastVoteStatus(status: VoteStatus): VoteStatus =
    broadcast(GiveUpVoteStatus(status))
    status

  private implicit def getConnectedPlayerByConn(conn: WebSocket): ConnectedPlayer =
    connectedPlayers.asScala.find(_.conn == conn).get // unsafe
end Game

object Game:
  private val s3Storage = S3Storage()
