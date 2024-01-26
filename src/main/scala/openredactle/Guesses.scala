package openredactle

import com.raquo.laminar.api.L.{*, given}

case class Guess(word: String)

object Guesses:
  private val guessedWords: Var[Set[Guess]] = Var(Set())

  private def addGuess(guess: Guess): Unit =
    guessedWords.update(_ + guess)

  private def renderGuess(index: Int, initial: Guess, guessSignal: Signal[Guess]): Element =
    div(
      span(index + 1),
      child.text <-- guessSignal.map(_.word),
    )

  def renderElement: Element =
    val guessInput = Var("")

    div(
      width := "20rem",

      form(
        width := "100%",
        display := "flex",

        onSubmit
          .preventDefault
          .mapTo(guessInput.now()) --> (w => addGuess(Guess(w))),

        input(
          flexGrow := 1,
          value <-- guessInput,
          onInput.mapToValue --> guessInput,
        ),
        button("Guess"),
      ),

      hr(),

      children <-- guessedWords.signal
        .splitByIndex(renderGuess)
        .map(_.toSeq.reverse)
    )
