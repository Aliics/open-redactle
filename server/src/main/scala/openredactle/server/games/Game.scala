package openredactle.server.games

import openredactle.server.data.{dummyRabbitArticleData, freeWords, randomWords}
import openredactle.server.send
import openredactle.shared.data.Word.*
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.logging.ImplicitLazyLogger
import openredactle.shared.message.Message
import openredactle.shared.message.Message.{GuessMatch, NewGuess}
import org.java_websocket.WebSocket

import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions
import scala.math.random

class Game extends ImplicitLazyLogger:
  private[games] val connectedPlayers = ConcurrentLinkedQueue[WebSocket]()
  private[games] val lastDisconnectTime = AtomicLong(Instant.now().toEpochMilli)

  private val fullArticleData = dummyRabbitArticleData

  val guessedWords: ConcurrentLinkedQueue[Guess] = ConcurrentLinkedQueue[Guess]()

  val id: String =
    def rng = random * (randomWords.length - 1)
    (0 to 2)
      .map(_ => randomWords(rng.toInt))
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
    if !guess.isBlank && !alreadyGuessed then
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
      broadcast(NewGuess(guess, matchedCount))
      matches.foreach:
        case (word, matches) =>
          broadcast(GuessMatch(word, matches))

  def articleData: List[ArticleData] =
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
