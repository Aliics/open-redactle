package openredactle.server.data

import openredactle.shared.data.Word
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class wordTest extends AnyFunSuite with Matchers:
  test("one word should create one Known Word"):
    wordsFromString("bunny") shouldBe Seq(Word.Known("bunny", true))

  test("many words should create multiple Known Words"):
    wordsFromString("bunnies are awesome") shouldBe Seq(Word.Known("bunnies", true), Word.Known("are", true), Word.Known("awesome", true))

  test("word with full stop should create Word and Punctuation"):
    wordsFromString("spuds.") shouldBe Seq(Word.Known("spuds", false), Word.Punctuation('.', true))

  test("multiple sentences should create a complex series of Words"):
    wordsFromString("that is... because spuds are amazing, and you should know it! number 2") shouldBe Seq(
      Word.Known("that", true),
      Word.Known("is", false),
      Word.Punctuation('.', false),
      Word.Punctuation('.', false),
      Word.Punctuation('.', true),
      Word.Known("because", true),
      Word.Known("spuds", true),
      Word.Known("are", true),
      Word.Known("amazing", false),
      Word.Punctuation(',', true),
      Word.Known("and", true),
      Word.Known("you", true),
      Word.Known("should", true),
      Word.Known("know", true),
      Word.Known("it", false),
      Word.Punctuation('!', true),
      Word.Known("number", true),
      Word.Known("2", true),
    )