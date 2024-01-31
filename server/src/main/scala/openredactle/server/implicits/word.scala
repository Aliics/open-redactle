package openredactle.server.implicits

import openredactle.shared.data.Word

import scala.language.implicitConversions

implicit def known(strs: String): Seq[Word] =
  strs.split(" ").map(Word.Known(_))
