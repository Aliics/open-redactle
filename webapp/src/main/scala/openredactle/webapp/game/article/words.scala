package openredactle.webapp.game.article

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.data.Word
import openredactle.shared.roughEquals
import openredactle.webapp.game.{Article, Game}
import openredactle.webapp.{Colors, solidBorder, userSelect}

type WordFoldState = (Seq[Element], Option[(String, Boolean)])

def renderWordElement(word: Word, section: Int, num: Int): Element =
  def spaceNodeWhen(cond: => Boolean): Node = if cond then " " else emptyNode

  word match
    case Word.Punctuation(c, hasSpace) =>
      span(c.toString, spaceNodeWhen(hasSpace))
    case Word.Known(str, hasSpace) =>
      def renderText =
        Article.selectedGuess.signal.map:
          case Some(selectedGuess) if roughEquals(str)(selectedGuess) =>
            span(
              borderBottom := solidBorder(),
              backgroundColor := Colors.highlightedWord,
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
            lengthStr + nbsp.repeat(length - lengthStr.length)

      def blockedElement(inHintMode: Boolean, isSecret: Boolean) = span(
        cls := ("blocked-word" +:
          (if inHintMode then
            if isSecret then Seq("is-secret")
            else Seq("is-hint")
          else Nil)),

        onClick --> (_ =>
          if !inHintMode || isSecret then showingLength.update(!_)
          else
            Game.requestHint(section, num)
            Article.inHintMode.update(_ => false)
          ),

        child <-- renderText,
      )

      span(
        child <-- Article.inHintMode.signal
          .combineWith(Article.secretPositions.signal)
          .map: (inHintMode, secretPositions) =>
            val isSecret = secretPositions.find(_._1 == section).exists(_._2.exists(_ == num))
            if hasSpace then span(blockedElement(inHintMode, isSecret), " ")
            else blockedElement(inHintMode, isSecret)
      )

