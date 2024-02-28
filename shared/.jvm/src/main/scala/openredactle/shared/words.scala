package openredactle.shared

import openredactle.shared.data.Word

import scala.collection.mutable

def wordsFromString(strs: String): Seq[Word] =
  strs
    .split(" ")
    .flatMap: s =>
      val strs = mutable.ListBuffer[Word]()
      var current = ""
      def applyCurrent(hasSpace: Boolean) =
        if current.nonEmpty then strs += Word.Known(current, hasSpace)

      for (c, i) <- s.zipWithIndex do
        if c.isLetterOrDigit || c == '\'' then current = current + c
        else
          applyCurrent(hasSpace = false)
          strs += Word.Punctuation(c, hasSpace = i == s.length - 1)
          current = ""

      applyCurrent(hasSpace = true)

      strs.toList
