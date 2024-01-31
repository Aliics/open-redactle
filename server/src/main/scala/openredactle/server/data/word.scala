package openredactle.server.data

import openredactle.shared.data.Word

import scala.collection.mutable

def wordsFromString(strs: String): Seq[Word] =
  strs
    .split(" ")
    .flatMap: s =>
      val strs = mutable.ListBuffer[Word]()
      var current = ""
      def applyCurrent() = if current.nonEmpty then strs += Word.Known(current)

      for c <- s do
        if c.isLetterOrDigit then current = current + c
        else
          applyCurrent()
          strs += Word.Punctuation(c)
          current = ""

      applyCurrent()

      strs.toList
