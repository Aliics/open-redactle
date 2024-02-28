package openredactle.server.data

import openredactle.shared.data.ArticleData.{Header, Paragraph, Title}
import openredactle.shared.data.{ArticleData, Word}
import openredactle.shared.{data, roughEquals, sumBy, wordsFromString}

val freeWords = List("or", "as", "a", "of", "and", "in", "the", "by", "if", "to", "be", "is")

def getMatchingGuessData(fullArticleData: Seq[ArticleData])(guess: String): (Map[Word.Known, Seq[(Int, Seq[Int])]], Int) =
  val matches = fullArticleData.map: articleData =>
    articleData.words.zipWithIndex.collect:
      case (known: Word.Known, idx) if roughEquals(known.str)(guess) => known -> idx
  .map(_.groupMap(_._1)(_._2))
  .zipWithIndex
  .filter(_._1.nonEmpty)
  .flatMap: (strs, i) =>
    strs.map((s, is) => s -> (i, is))
  .groupMap(_._1)(_._2)

  val matchedCount = matches.sumBy(_._2.sumBy(_._2.size))

  matches -> matchedCount
  