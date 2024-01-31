package openredactle.server.games

import com.typesafe.scalalogging.Logger
import openredactle.server.data.wordsFromString
import openredactle.server.send
import openredactle.shared.data.ArticleData.*
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
  private given logger: Logger = Logger[Game]
  
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

  private val knownWords = ConcurrentSkipListSet[String](List(
    "or", "as", "a", "of", "and", "in", "the", "by", "if", "to",
  ).asJava)

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

  private val fullArticleData: List[ArticleData] = let:
    List(
      Title(wordsFromString("Domestic rabbit")),
      Paragraph(wordsFromString("The domestic or domesticated rabbit—more commonly known as a pet rabbit, bunny, bun, or bunny rabbit—is the domesticated form of the European rabbit, a member of the lagomorph order. A male rabbit is known as a buck, a female is a doe, and a young rabbit is a kit, or kitten.")),
      Paragraph(wordsFromString("Rabbits were first used for their food and fur by the Romans, and have been kept as pets in Western nations since the 19th century. Rabbits can be housed in exercise pens, but free roaming without any boundaries in a rabbit-proofed space has become popularized on social media in recent years. Beginning in the 1980s, the idea of the domestic rabbit as a house companion, a so-called house rabbit similar to a house cat, was promoted. Rabbits can be litter box-trained and taught to come when called, but they require exercise and can damage a house that has not been \"rabbit proofed\" based on their innate need to chew. Accidental interactions between pet rabbits and wild rabbits, while seemingly harmless, are usually strongly discouraged due to the species' different temperaments as well as wild rabbits potentially carrying diseases.")),
      Paragraph(wordsFromString("Unwanted pet rabbits end up in animal shelters, especially after the Easter season (see Easter Bunny). In 2017, they were the United States' third most abandoned pet. Some of them go on to be adopted and become family pets in various forms. Because their wild counterparts have become invasive in Australia, pet rabbits are banned in the state of Queensland. Pet rabbits, being a domesticated breed that lack survival instincts, do not fare well in the wild if they are abandoned or escape from captivity.")),

      Header(wordsFromString("History")),
      Paragraph(wordsFromString("Phoenician sailors visiting the coast of Spain c. 12th century BC, mistaking the European rabbit for a species from their homeland (the rock hyrax Procavia capensis), gave it the name i-shepan-ham (land or island of hyraxes).")),
      Paragraph(wordsFromString("The captivity of rabbits as a food source is recorded as early as the 1st century BC, when the Roman writer Pliny the Elder described the use of rabbit hutches, along with enclosures called leporaria. A controversial theory is that a corruption of the rabbit's name used by the Romans became the Latin name for the peninsula, Hispania. In Rome, rabbits were raised in large walled colonies with walls extended underground. According to Pliny, the consumption of unborn and newborn rabbits, called laurices, was considered a delicacy.")),
      Paragraph(wordsFromString("Evidence for the domestic rabbit is rather late. In the Middle Ages, wild rabbits were often kept for the hunt. Monks in southern France were crossbreeding rabbits at least by the 12th century AD. Domestication was probably a slow process that took place from the Roman period (or earlier) until the 1500s.")),
    )

  def articleData: List[ArticleData] =
    fullArticleData.map: articleData =>
      articleData.copy(words = articleData.words.collect:
        case Known(word) =>
          if knownWords.asScala.exists(_ equalsIgnoreCase word) then Known(word)
          else Unknown(word.length)
      )
