package openredactle.shared

import scala.util.Random

extension [A](seq: Seq[A])
  def random: A =
    seq(Random.nextInt(seq.length))
