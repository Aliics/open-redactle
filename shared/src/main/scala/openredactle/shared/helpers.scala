package openredactle.shared

def let[A](thunk: => A): A = thunk

def roughEquals(left: String)(right: String) =
  def anySuffixCombination(suffixes: String*) =
    val matching =
      for a <- suffixes
          b <- suffixes
      yield !(left equalsIgnoreCase a)
        && !(right equalsIgnoreCase b)
        && (left.stripSuffix(a) equalsIgnoreCase right.stripSuffix(b))
    matching contains true

  left.equalsIgnoreCase(right) ||
    anySuffixCombination("s", "es", "'s") ||
    anySuffixCombination("ies", "y")
