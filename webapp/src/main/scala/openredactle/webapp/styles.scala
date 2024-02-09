package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.KeySetter

val userSelect: StyleProp[String] = styleProp("user-select")

def solidBorder(color: String = Colors.black): String =
  s"solid 1px $color"

def buttonStyle(fgColor: String = Colors.white, bgColor: String = Colors.black): Seq[KeySetter.StyleSetter] =
  Seq(
    cursor := "pointer",
    color := fgColor,
    backgroundColor := bgColor,
    marginLeft := "0.5rem",
    border := "none",
    padding := "0.25rem 0.5rem",
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
  val black = "var(--black)"
  val white = "var(--white)"
  val mainBackground = "var(--main-background)"
  val highlightedWord = "var(--highlighted-word)"
  val highlightedHintWord = "var(--highlighted-hint-word)"
  val correctWord = "var(--correct-word)"
  val hintWord = "var(--hint-word)"
  val danger = "var(--danger)"
  val action = "var(--action)"
  val actionHeld = "var(--action-held)"
  val secretWord = "var(--secret-word)"
  val hintBlock = "var(--hint-block)"

  def highlightColor(isHint: Boolean): String =
    if isHint then Colors.highlightedHintWord
    else Colors.highlightedWord

  def wordColor(isCorrectGuess: Boolean, isHint: Boolean): String =
    if isCorrectGuess then
      if isHint then Colors.hintWord
      else Colors.correctWord
    else Colors.mainBackground
