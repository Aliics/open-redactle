package openredactle.scraper

import scala.util.CommandLineParser.FromString

enum Mode:
  case Generate
  case Update

object Mode:
  given FromString[Mode] with
    def fromString(s: String): Mode = Mode valueOf s

