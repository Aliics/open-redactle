package openredactle.server.data

import openredactle.shared.data.ArticleData.{Paragraph, Title}
import openredactle.shared.data.Word.Known
import openredactle.shared.wordsFromString
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class articleTest extends AnyFunSuite with Matchers:
  private val articleData = Seq(
    Title(wordsFromString("French Bread")),
    Paragraph(wordsFromString("French bread is really good bread normally.")),
  )

  test("should have all guesses across multiple paragraphs"):
    getMatchingGuessData(articleData)("bread") shouldBe
      (Map(
        Known("Bread", hasSpace = true) -> Seq((0, Seq(1))),
        Known("bread", hasSpace = true) -> Seq((1, Seq(1, 5)))
      ), 3)

  test("should not have any guesses when word is non-sense"):
    getMatchingGuessData(articleData)("dragonsbreath") shouldBe
      (Map(), 0)
