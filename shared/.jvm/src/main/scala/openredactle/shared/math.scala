package openredactle.shared

extension [I <: Iterable[A], A](cc: I)
  def sumBy(f: A => Int): Int = cc.map(f).sum
