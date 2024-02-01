package openredactle.webapp.data

import com.raquo.laminar.api.L.{*, given}
import openredactle.shared.data.Word
import openredactle.webapp.userSelect

extension (word: Word)
  def renderElement: Element =
    def spaceNodeWhen(cond: => Boolean): Node = if cond then " " else emptyNode
    word match
      case Word.Punctuation(c, hasSpace) =>
        span(c.toString, spaceNodeWhen(hasSpace))
      case Word.Known(str, hasSpace) =>
        span(str, spaceNodeWhen(hasSpace))
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
          backgroundColor := "black",
          color := "white",
          userSelect := "none",
          cursor := "pointer",

          onClick --> (_ => showingLength.update(!_)),

          child <-- renderText,
        )

        if hasSpace then span(blockedElement, " ")
        else blockedElement
