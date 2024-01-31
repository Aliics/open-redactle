package openredactle.server.games

import openredactle.server.implicits.given
import openredactle.server.send
import openredactle.shared.data.ArticleData.{Paragraph, Title}
import openredactle.shared.data.Word.*
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.let
import openredactle.shared.message.Message
import openredactle.shared.message.Message.{GuessMatch, NewGuess}
import org.java_websocket.WebSocket

import java.util.concurrent.{ConcurrentLinkedQueue, ConcurrentSkipListSet}
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions
import scala.math.random

class Game:
  val id: String =
    def rng = random * (randomWords.length - 1)
    (0 to 2)
      .map(_ => randomWords(rng.toInt))
      .mkString("-")

  private[games] val connectedPlayers = ConcurrentLinkedQueue[WebSocket]()

  def connect(conn: WebSocket): Int =
    broadcast(Message.PlayerJoined(connectedPlayers.size))
    connectedPlayers.add(conn)
    connectedPlayers.size

  def disconnect(conn: WebSocket): Unit =
    connectedPlayers.asScala
      .zipWithIndex
      .find(_._1 == conn)
      .foreach: (conn, idx) =>
        broadcast(Message.PlayerLeft(idx))
        connectedPlayers.remove(idx)

  private def broadcast(message: Message): Unit =
    connectedPlayers.asScala.foreach(_.send(message))

  private val knownWords = ConcurrentSkipListSet[String](List("dolor", "sit").asJava)

  def addGuess(guess: String): Unit =
    val added = knownWords add guess

    if added then
      val matches = fullArticleData.map: articleData =>
        articleData.words.zipWithIndex.collect:
          case (Known(word), idx) if word equalsIgnoreCase guess => word -> idx
      .map(_.groupMap(_._1)(_._2))
      .zipWithIndex
      .filter(_._1.nonEmpty)
      .flatMap: (strs, i) =>
        strs.map((s, is) => s -> (i, is))
      .groupMap(_._1)(_._2)

      broadcast(NewGuess(guess, matches.map(_._2.map(_._2.length).sum).sum))
      matches.foreach:
        case (word, matches) =>
          broadcast(GuessMatch(word, matches))

  private val fullArticleData: List[ArticleData] =
    List(
      Title("Lorem Ipsum"),
      Paragraph("Lorem ipsum dolor sit amet")
    )

  def articleData: List[ArticleData] =
    fullArticleData.map: articleData =>
      articleData.copy(words = articleData.words.collect:
        case Known(word) =>
          if knownWords.asScala.exists(_ equalsIgnoreCase word) then Known(word)
          else Unknown(word.length)
      )
