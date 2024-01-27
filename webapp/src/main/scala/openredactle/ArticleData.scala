package openredactle

import com.raquo.laminar.api.L.{*, given}

enum ArticleData(val words: Seq[Word]):
  case Title(override val words: Seq[Word]) extends ArticleData(words)
  case Header(override val words: Seq[Word]) extends ArticleData(words)
  case Paragraph(override val words: Seq[Word]) extends ArticleData(words)

enum Word:
  case Known(str: String)
  case Unknown(length: Int)

  def element: Element =
    this match
      case Word.Known(str) => span(str)
      case Word.Unknown(length) => span(backgroundColor := "gray", nbsp repeat length)
