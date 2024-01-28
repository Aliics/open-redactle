package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}

case class Guess(word: String)

object Guesses:
  private val guessedWords: Var[List[Guess]] = Var(List())

  def renderElement: Element =
    div(
      width := "16rem",
      height := "100%",
      display := "flex",
      flexDirection := "column",
      borderLeft := "solid 1px black",

      renderGuessForm,

      div(
        flexGrow := 1,
        overflowY := "scroll",

        children <-- guessedWords.signal
          .splitByIndex(renderGuess)
          .map(_.reverse),
      ),
    )

  private def renderGuessForm: Element =
    val guessInput = Var("")

    form(
      display := "flex",
      padding := "1rem",
      borderBottom := "solid 1px black",

      onSubmit
        .preventDefault
        .mapTo(guessInput.now()) --> submitGuess,

      input(
        flexGrow := 1,
        value <-- guessInput,

        onInput.mapToValue --> guessInput,
      ),
      button("Guess"),
    )

  private def submitGuess(input: String): Unit =
    val guess = Guess(input)
    guessedWords.update: guesses =>
      if guess.word.isBlank || guesses.contains(guess) then guesses
      else guesses :+ guess

  private def renderGuess(index: Int, initial: Guess, guessSignal: Signal[Guess]): Element =
    div(
      display := "flex",
      alignItems := "center",
      borderBottom := "solid 1px black",
      minWidth := "fit-content",
      fontSize := "14px",
      padding := "0.25rem",

      span(
        fontSize := "13px",
        color := "grey",
        marginRight := "0.5rem",

        index + 1,
      ),
      child.text <-- guessSignal.map(_.word),
    )
