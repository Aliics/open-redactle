package openredactle.server.data

// Needed so Comparable can be implemented.
// We use a ConcurrentSkipListSet, which needs all elements to implement it.
case class Guess(word: String, matchedCount: Int, isHint: Boolean) extends Comparable[Guess]:
  override def compareTo(o: Guess): Int =
    val wordCmp = word compareTo o.word
    if wordCmp != 0 then wordCmp
    else
      val matchedCmp = matchedCount compareTo o.matchedCount
      if matchedCmp != 0 then matchedCmp
      else isHint compareTo o.isHint
