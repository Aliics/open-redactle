package openredactle.webapp.data

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.userSelect

enum Word:
  case Known(str: String)
  case Unknown(length: Int)

  def renderElement: Element = this match
    case Word.Known(str) => span(str)
    case Word.Unknown(length) =>
      val showingLength = Var(false)

      def renderText: Signal[TextNode] =
        showingLength.signal.map: showingLength =>
          val blanked = nbsp repeat length
          if !showingLength then blanked
          else
            val lengthStr = length.toString
            lengthStr + nbsp.repeat(length - lengthStr.length)

      span(
        backgroundColor := "black",
        color := "white",
        userSelect := "none",
        cursor := "pointer",

        onClick --> (_ => showingLength.update(!_)),

        child <-- renderText,
      )
