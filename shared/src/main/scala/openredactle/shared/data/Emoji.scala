package openredactle.shared.data

import upickle.default.ReadWriter

// New Scala3 enums are being a little silly with upickle.
// Had to use the Scala2-style enums.
sealed trait Emoji(val code: String)derives ReadWriter
object Emoji:
  def values: Seq[Emoji] = Seq(Dog, Cat, Mouse, Rabbit, Fox, Bear, Koala, Tiger, Lion, Cow, Pig, Frog, Monkey, Chicken,
    Penguin, Chick, Duck, Eagle, Owl, Bat, Wolf, Horse, Bee, Snail, Ladybug, Turtle, Crocodile, Sheep, Parrot)

  def valueOf(s: String): Emoji = values.find(_.toString == s).get

case object Dog extends Emoji("\uD83D\uDC36")
case object Cat extends Emoji("\uD83D\uDC31")
case object Mouse extends Emoji("\uD83D\uDC2D")
case object Rabbit extends Emoji("\uD83D\uDC30")
case object Fox extends Emoji("\uD83E\uDD8A")
case object Bear extends Emoji("\uD83D\uDC3B")
case object Koala extends Emoji("\uD83D\uDC28")
case object Tiger extends Emoji("\uD83D\uDC2F")
case object Lion extends Emoji("\uD83E\uDD81")
case object Cow extends Emoji("\uD83D\uDC2E")
case object Pig extends Emoji("\uD83D\uDC37")
case object Frog extends Emoji("\uD83D\uDC38")
case object Monkey extends Emoji("\uD83D\uDC35")
case object Chicken extends Emoji("\uD83D\uDC14")
case object Penguin extends Emoji("\uD83D\uDC27")
case object Chick extends Emoji("\uD83D\uDC24")
case object Duck extends Emoji("\uD83E\uDD86")
case object Eagle extends Emoji("\uD83E\uDD85")
case object Owl extends Emoji("\uD83E\uDD89")
case object Bat extends Emoji("\uD83E\uDD87")
case object Wolf extends Emoji("\uD83D\uDC3A")
case object Horse extends Emoji("\uD83D\uDC34")
case object Bee extends Emoji("\uD83D\uDC1D")
case object Snail extends Emoji("\uD83D\uDC0C")
case object Ladybug extends Emoji("\uD83D\uDC1E")
case object Turtle extends Emoji("\uD83D\uDC22")
case object Crocodile extends Emoji("\uD83D\uDC0A")
case object Sheep extends Emoji("\uD83D\uDC11")
case object Parrot extends Emoji("\uD83E\uDD9C")
