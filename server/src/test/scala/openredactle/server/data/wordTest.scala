package openredactle.server.data

import openredactle.shared.data.Word
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class wordTest extends AnyFunSuite with Matchers:
  test("one word should create one Known Word"):
    wordsFromString("bunny") shouldBe Seq(Word.Known("bunny"))

  test("many words should create multiple Known Words"):
    wordsFromString("bunnies are awesome") shouldBe Seq(Word.Known("bunnies"), Word.Known("are"), Word.Known("awesome"))

  test("word with full stop should create Word and Punctuation"):
    wordsFromString("spuds.") shouldBe Seq(Word.Known("spuds"), Word.Punctuation('.'))

  test("multiple sentences should create a complex series of Words"):
    wordsFromString("that is... because spuds are amazing, and you should know it! number 2") shouldBe Seq(
      Word.Known("that"),
      Word.Known("is"),
      Word.Punctuation('.'),
      Word.Punctuation('.'),
      Word.Punctuation('.'),
      Word.Known("because"),
      Word.Known("spuds"),
      Word.Known("are"),
      Word.Known("amazing"),
      Word.Punctuation(','),
      Word.Known("and"),
      Word.Known("you"),
      Word.Known("should"),
      Word.Known("know"),
      Word.Known("it"),
      Word.Punctuation('!'),
      Word.Known("number"),
      Word.Known("2"),
    )