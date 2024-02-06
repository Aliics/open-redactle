package openredactle.server.games

import openredactle.server.data.{freeWords, randomWords}
import openredactle.server.{random, send}
import openredactle.shared.data.Word.*
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.let
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.message.Message
import openredactle.shared.message.Message.{GameWon, GuessMatch, NewGuess}
import openredactle.shared.stored.S3Storage
import org.java_websocket.WebSocket

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions

class Game extends ImplicitLazyLogger:
  private[games] val connectedPlayers = ConcurrentLinkedQueue[WebSocket]()
  private[games] val lastDisconnectTime = AtomicLong(Instant.now().toEpochMilli)

  private val gameWon = AtomicBoolean(false)
  private val fullArticleData = let:
    val indexData = Game.s3Storage.fetchIndex()
    val (i, _) = indexData.random
    Game.s3Storage.getArticleByIndex(i)

  val guessedWords: ConcurrentLinkedQueue[Guess] = ConcurrentLinkedQueue[Guess]()

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

  def addGuess(guess: String): Unit =
    val alreadyGuessed = guessedWords.asScala.exists(_._1 == guess)
    val isFreeWord = freeWords.exists(_ equalsIgnoreCase  guess)
    if !guess.isBlank && !isFreeWord && !alreadyGuessed then
      val matches = fullArticleData.map: articleData =>
        articleData.words.zipWithIndex.collect:
          case (known: Known, idx) if known.str equalsIgnoreCase guess => known -> idx
      .map(_.groupMap(_._1)(_._2))
      .zipWithIndex
      .filter(_._1.nonEmpty)
      .flatMap: (strs, i) =>
        strs.map((s, is) => s -> (i, is))
      .groupMap(_._1)(_._2)

      val matchedCount = matches.map(_._2.map(_._2.length).sum).sum

      guessedWords.add(Guess(guess, matchedCount))

      if articleData.head.words.exists(_.isInstanceOf[Word.Unknown]) then
        broadcast(NewGuess(guess, matchedCount))
        matches.foreach:
          case (word, matches) =>
            broadcast(GuessMatch(word, matches))
      else
        broadcast(GameWon(fullArticleData))
        gameWon.set(true)

  def articleData: Seq[ArticleData] =
    if gameWon.get() then fullArticleData
    else
      fullArticleData.map: articleData =>
        articleData.copy(words = articleData.words.collect:
          case p: Punctuation => p
          case known@Known(str, hasSpace) =>
            val matchedGuessedWords = guessedWords.asScala.filter(_.matchedCount > 0).map(_._1)
            if (freeWords ++ matchedGuessedWords).exists(_ equalsIgnoreCase str) then known
            else Unknown(str.length, hasSpace)
        )

  // Needed so Comparable can be implemented.
  // We use a ConcurrentSkipListSet, which needs all elements to implement it.
  case class Guess(word: String, matchedCount: Int) extends Comparable[Guess]:
    override def compareTo(o: Guess): Int =
      val wordCmp = word compareTo o.word
      if wordCmp != 0 then wordCmp
      else matchedCount compareTo o.matchedCount

object Game:
  private val s3Storage = S3Storage()
