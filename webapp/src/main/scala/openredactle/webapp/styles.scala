package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter

val userSelect: StyleProp[String] = styleProp("user-select")

def solidBorder(color: String = Colors.tertiary): String =
  s"solid 1px $color"

def colored(fgColor: String = Colors.primary, bgColor: String = Colors.secondary): Seq[KeySetter.StyleSetter] =
  Seq(
    color := fgColor,
    backgroundColor := bgColor,
  )

def centeredScreen: Seq[KeySetter.StyleSetter] =
  Seq(
    position := "absolute",
    width := "100%",
    height := "100%",
    top := "0",

    textAlign := "center",
    justifyContent := "center",
  ) ++ layoutFlex("column")

def layoutFlex(direction: String = "default"): Seq[KeySetter.StyleSetter] =
  Seq(
    display := "flex",
    flexDirection := direction,
  )

object Colors:
  val primary = "var(--primary)"
  val secondary = "var(--secondary)"
  val tertiary = "var(--tertiary)"
  val mainBackground = "var(--main-background)"
  val danger = "var(--danger)"
  val action = "var(--action)"
  val actionHeld = "var(--action-held)"
  val secretWord = "var(--secret-word)"
  val hintBlock = "var(--hint-block)"

  private val highlightedWord = "var(--highlighted-word)"
  private val highlightedHintWord = "var(--highlighted-hint-word)"
  private val correctWord = "var(--correct-word)"
  private val hintWord = "var(--hint-word)"

  def highlightColor(isHint: Boolean): String =
    if isHint then Colors.highlightedHintWord
    else Colors.highlightedWord

  def wordColor(isCorrectGuess: Boolean, isHint: Boolean): String =
    if isCorrectGuess then
      if isHint then Colors.hintWord
      else Colors.correctWord
    else Colors.mainBackground
