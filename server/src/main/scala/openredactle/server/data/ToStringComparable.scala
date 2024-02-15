package openredactle.server.data

/**
 * Used to compare objects as strings. Very naive and only works in some cases.
 *
 * @tparam T The extending class type
 */
trait ToStringComparable[T] extends Comparable[T]:
  override def compareTo(o: T): Int =
    toString compareTo o.toString
