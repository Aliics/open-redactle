package openredactle.webapp.game.article

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.data.Word
import openredactle.shared.roughEquals
import openredactle.webapp.game.{Article, Game}
import openredactle.webapp.{Colors, solidBorder}

def renderWordElement(word: Word, section: Int, num: Int): Element =
  def spaceNodeWhen(cond: => Boolean): Node = if cond then " " else emptyNode

  word match
    case Word.Punctuation(c, hasSpace) =>
      span(c.toString, spaceNodeWhen(hasSpace))
    case Word.Known(str, hasSpace) =>
      def renderText =
        Article.selectedGuess.signal.map:
          case Some(selectedGuess -> isHint) if roughEquals(str)(selectedGuess) =>
            span(
              borderBottom := solidBorder(),
              backgroundColor := Colors.highlightColor(isHint),
              str,
            )
          case _ =>
            span(str)

      span(
        child <-- renderText,
        spaceNodeWhen(hasSpace),
      )
    case Word.Unknown(length, hasSpace) =>
      val showingLength = Var(false)

      def renderText: Signal[TextNode] =
        showingLength.signal.map: showingLength =>
          val blanked = nbsp repeat length
          if !showingLength then blanked
          else
            val lengthStr = length.toString
            lengthStr + nbsp.repeat(length - lengthStr.size)

      def blockedElement(isSecret: Boolean) = span(
        cls := (Seq("blocked-word") ++ Option.when(isSecret)("is-secret")),

        onClick --> (_ =>
          if !Article.inHintMode.now() || isSecret then showingLength.update(!_)
          else
            Game.requestHint(section, num)
            Article.inHintMode.set(false)
          ),

        child <-- renderText,
      )

      span(
        child <-- Article.secretPositions.signal
          .map: secretPositions =>
            val isSecret = secretPositions.find(_._1 == section).exists(_._2.exists(_ == num))
            if hasSpace then span(blockedElement(isSecret), " ")
            else blockedElement(isSecret)
      )

