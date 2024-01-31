package openredactle.shared.data

import upickle.default.ReadWriter

enum ArticleData(val words: Seq[Word]) extends Product derives ReadWriter:
  case Title(override val words: Seq[Word]) extends ArticleData(words)
  case Header(override val words: Seq[Word]) extends ArticleData(words)
  case Paragraph(override val words: Seq[Word]) extends ArticleData(words)

  def copy(words: Seq[Word]): ArticleData = this match
    case ArticleData.Title(_) => ArticleData.Title(words)
    case ArticleData.Header(_) => ArticleData.Header(words)
    case ArticleData.Paragraph(_) => ArticleData.Paragraph(words)
