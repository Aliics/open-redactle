package openredactle.webapp.settings

import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.data.Emoji
import openredactle.shared.random
import openredactle.webapp.:=
import openredactle.webapp.element.RenderableElement
import openredactle.webapp.game.Game
import org.scalajs.dom.window.localStorage

import scala.annotation.tailrec

object EmojiSelector extends RenderableElement:
  private val emojiKey = "emoji"

  private lazy val emojiSelected = Var:
    ensureRandomEmojiChosen()
    localStorage.getItem(emojiKey)

  def storedEmoji: Emoji = Emoji valueOf storedEmojiString

  @tailrec def ensureRandomEmojiChosen(): Unit =
    Option(storedEmojiString) match
      case Some(value) =>
        if Emoji.values.exists(_.toString == value)
        then println(s"Emoji is chosen: $value")
        else
          localStorage.removeItem(emojiKey)
          ensureRandomEmojiChosen()
      case None =>
        storeEmoji(Emoji.values.random)

  override lazy val renderElement: Element =
    val sel = select(
      idAttr := "emoji",
      fontSize := "16px",
      height := "2.5rem",
      width := "5rem",
      cursor := "pointer",

      value <-- emojiSelected,

      Emoji.values.map: emoji =>
        option(
          value := emoji.toString,
          emoji.code,
        ),
    )

    sel.ref.onchange = _ =>
      emojiSelected := sel.ref.value

      val emoji = Emoji valueOf sel.ref.value

      storeEmoji(emoji)

      if Game.gameId.now().isDefined
      then Game.changeEmojiInGame(emoji)

    sel

  private def storeEmoji(emoji: Emoji): Unit =
    localStorage.setItem(emojiKey, emoji.toString)

  private def storedEmojiString: String =
    localStorage.getItem(emojiKey)
