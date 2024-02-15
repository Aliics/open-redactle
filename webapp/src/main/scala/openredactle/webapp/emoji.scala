package openredactle.webapp

import openredactle.shared.data.Emoji
import org.scalajs.dom.window.localStorage
import openredactle.shared.random

private val emojiKey = "emoji"

def storedEmoji: Emoji = Emoji valueOf storedEmojiString

def storedEmojiString: String =
  localStorage.getItem(emojiKey)

def storeEmoji(emoji: Emoji): Unit =
  localStorage.setItem(emojiKey, emoji.toString)

def ensureRandomEmojiChosen(): Unit =
  Option(storedEmojiString) match
    case Some(value) =>
      if Emoji.values.exists(_.toString == value)
      then println(s"Emoji is chosen: $value")
      else
        localStorage.removeItem(emojiKey)
        ensureRandomEmojiChosen()
    case None =>
      storeEmoji(Emoji.values.random)
