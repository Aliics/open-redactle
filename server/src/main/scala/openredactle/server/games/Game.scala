package openredactle.server.games

import openredactle.server.data.{Guess, freeWords, getMatchingGuessData, randomWords}
import openredactle.server.send
import openredactle.shared.data.Word.*
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.message.Message
import openredactle.shared.message.Message.*
import openredactle.shared.stored.S3Storage
import openredactle.shared.{data, let, random, roughEquals}
import org.java_websocket.WebSocket

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicLong}
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions

class Game extends ImplicitLazyLogger:
  private[games] val connectedPlayers = ConcurrentLinkedQueue[WebSocket]()
  private[games] val lastDisconnectTime = AtomicLong(Instant.now().toEpochMilli)

  private val gameWon = AtomicBoolean(false)
  private val fullArticleData = let:
    val indexData = Game.s3Storage.fetchIndex()
    val r@(i, _) = indexData.random
    logger.info(s"Selected article: $r")
    Game.s3Storage.getArticleByIndex(i)

  val guessedWords: ConcurrentLinkedQueue[Guess] = ConcurrentLinkedQueue[Guess]()
  val hintsAvailable: AtomicInteger = AtomicInteger(3)

  val id: String =
    (0 to 2)
      .map(_ => randomWords.random)
      .mkString("-")

  def connect(conn: WebSocket): Int =
    broadcast(Message.PlayerJoined())
    connectedPlayers.add(conn)
    connectedPlayers.size

  def disconnect(conn: WebSocket): Unit =
    lastDisconnectTime.set(Instant.now().toEpochMilli)
    connectedPlayers.remove(conn)
    broadcast(Message.PlayerLeft())

  private def broadcast(message: Message): Unit =
    connectedPlayers.asScala.foreach(_.send(message))

  def addGuess(rawGuess: String, isHint: Boolean = false)(using conn: WebSocket): Unit =
    val guess = rawGuess.trim
    val isFreeWord = freeWords.exists(_ equalsIgnoreCase guess)
    val alreadyGuessed = guessedWords.asScala.find(g => roughEquals(g._1)(guess))
    if !guess.isBlank && !isFreeWord && alreadyGuessed.isEmpty then
      val (matches, matchedCount) = getMatchingGuessData(fullArticleData)(guess)

      guessedWords.add(Guess(guess, matchedCount, isHint))

      logger.info(s"""${if isHint then "Hint" else "Guess"} in $id: "$guess"""")
      broadcast(NewGuess(guess, matchedCount, isHint))

      if articleData.head.words.exists(_.isInstanceOf[Word.Unknown]) then
        matches.foreach:
          case (word, matches) =>
            broadcast(GuessMatch(word, matches))
      else
        logger.info(s"""Game won $id with guess: "$guess"""")
        broadcast(GameWon(fullArticleData))
        gameWon.set(true)
    else if alreadyGuessed.isDefined then
      val Guess(word, _, isHint) = alreadyGuessed.get
      conn.send(AlreadyGuessed(word, isHint))

  def requestHint(section: Int, num: Int)(using WebSocket): Unit =
    val avail = hintsAvailable.get()
    if avail > 0 then
      hintsAvailable.set(avail - 1)
      fullArticleData.lift(section).flatMap(_.words.lift(num)) match
        case Some(Word.Known(str, _)) =>
          addGuess(str, isHint = true)
          broadcast(HintUsed())
        case _ =>
          logger.warn(s"Attempted to give hint at position: $section, $num")

  def articleData: Seq[ArticleData] =
    if gameWon.get() then fullArticleData
    else
      fullArticleData.map: articleData =>
        articleData.copy(words = articleData.words.collect:
          case p: Punctuation => p
          case known@Known(str, hasSpace) =>
            val matchedGuessedWords = guessedWords.asScala.filter(_.matchedCount > 0).map(_._1)
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

object Game:
  private val s3Storage = S3Storage()
