package openredactle.webapp

import openredactle.shared.data.Emoji
import org.scalajs.dom.window.localStorage
import openredactle.shared.random

private val emojiKey = "emoji"

def storedEmoji(): String =
  localStorage.getItem(emojiKey)

def storeEmoji(emoji: Emoji): Unit =
  localStorage.setItem(emojiKey, emoji.toString)

def ensureRandomEmojiChosen(): Unit =
  Option(storedEmoji()) match
    case Some(value) => println(s"Emoji is chosen: $value")
    case None =>
      storeEmoji(Emoji.values.random)
