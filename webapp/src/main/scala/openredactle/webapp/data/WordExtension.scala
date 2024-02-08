package openredactle.webapp.data

import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.data.Word
import openredactle.shared.roughEquals
import openredactle.webapp.{Colors, solidBorder, userSelect}

extension (word: Word)
  def renderElement(selectedGuessSignal: Signal[Option[String]]): Element =
    def spaceNodeWhen(cond: => Boolean): Node = if cond then " " else emptyNode
    word match
      case Word.Punctuation(c, hasSpace) =>
        span(c.toString, spaceNodeWhen(hasSpace))
      case Word.Known(str, hasSpace) =>
        def renderText =
          selectedGuessSignal.map: selectedGuess =>
            val isSelected = roughEquals(str)(selectedGuess.getOrElse(""))
            if !isSelected then span(str)
            else span(
              borderBottom := solidBorder(),
              backgroundColor := Colors.highlightedWord,
              str,
            )

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

        val blockedElement = span(
          backgroundColor := Colors.black,
          color := "white",
          userSelect := "none",
          cursor := "pointer",

          onClick --> (_ => showingLength.update(!_)),

          child <-- renderText,
        )

        if hasSpace then span(blockedElement, " ")
        else blockedElement
