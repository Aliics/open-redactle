package openredactle.server.games

import org.java_websocket.WebSocket

import java.util.concurrent.ConcurrentLinkedQueue
import scala.jdk.CollectionConverters.*

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

// Thought it would be fun to have a curated list of words for random stuff.
val randomWords = List(
  "alpha", "alex", "andrew", "anthony", "angel", "apple", "android", "apathy",
  "bravo", "bob", "bored", "board", "broad", "beard", "banana", "batmitzvah", "bowie",
  "charlie", "cat", "charlie", "caleb", "car", "cookie", "colloquially", "cord", "computer",
  "delta", "dork", "david", "darrell", "dan", "dad", "dean", "dip", "dew",
  "echo", "ethan", "eat", "earth", "east", "eager", "elope", "enemy",
  "foxtrot", "frank", "far", "fort", "fortress", "fairy", "five", "fill", "favourite",
  "golf", "george", "greg", "good", "godot", "golang", "gopher", "grime",
  "india", "indoors", "inner", "inkling", "indira", "inter",
  "hotel", "honest", "hostel", "hill", "horror", "high",
  "juliette", "john", "jason", "james", "jog", "joy", "jumping", "joyous",
  "kilo", "kit", "kite", "kate", "kilometre", "knot", "king",
  "lima", "lord", "lari", "longest", "long", "lard", "lawn", "looking", "life",
  "mike", "more", "mars", "maverick", "moose", "maw", "moo", "mann",
  "november", "north", "not", "name", "net", "neat", "nap",
  "oscar", "oliver", "over", "otter", "oman", "orc", "ogre", "onion",
  "papa", "park", "put", "pudding", "pan", "part", "pain", "pole", "pterodactyl",
  "quebec", "quarter", "quit", "qux", "quotient", "quiet", "quick", "queen",
  "romeo", "room", "roam", "roof", "rage", "running", "rook", "roll", "rob",
  "sierra", "short", "sit", "suit", "sweet", "sort", "swift", "swam",
  "tango", "time", "tim", "torn", "thomas", "tangle", "trance", "truce",
  "uniform", "useful", "under", "underscore", "unless", "utmost", "ugly",
  "victor", "vice", "vain", "vein", "vienna", "veer", "vote",
  "whiskey", "win", "wits", "worst", "while", "winning", "worth", "whole",
  "xray", "xylophone",
  "yankee", "your", "you", "yam", "yeet", "yap",
  "zulu", "zillion", "zip", "zipper", "zit",
)
