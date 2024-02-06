package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.{Colors, buttonStyle, layoutFlex, solidBorder}

object Guesses:
  private type Guess = (String, Int)

  val guessedWords: Var[List[Guess]] = Var(List())

  def renderElement: Element =
    div(
      width := "22rem",
      height := "100%",
      layoutFlex("column"),
      borderLeft := solidBorder(),

      renderGuessForm,

      div(
        borderBottom := solidBorder(),
        fontSize := "12px",
        color := "dimgray",
        padding := "0.2rem 1rem",

        child.text <-- guessedWords.signal.map(w => s"Guesses: ${w.size}"),
      ),

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
      layoutFlex(),
      padding := "1rem",
      borderBottom := solidBorder(),

      onSubmit.preventDefault.mapTo(guessInput.now()) --> { w =>
        Game.addGuess(w)
        guessInput.update(_ => "")
      },

      input(
        flexGrow := 1,
        minWidth := "16rem",

        value <-- guessInput,
        onInput.mapToValue --> guessInput,
      ),
      button(
        buttonStyle(),
        "Guess",
      ),
    )

  private def renderGuess(index: Int, initial: Guess, guessSignal: Signal[Guess]): Element =
    div(
      layoutFlex(),
      alignItems := "center",
      borderBottom := solidBorder(),
      minWidth := "fit-content",
      fontSize := "14px",
      padding := "0.25rem",
      cursor := "pointer",

      backgroundColor <--
        Article.selectedGuess.signal.map:
          case Some(sel) if sel == initial._1 =>
            Colors.highlightedWord
          case _ => "#f1f1f1",

      onClick --> { _ =>
        Article.selectedGuess.update:
          case Some(sel) if sel == initial._1 => None
          case _ => Some(initial._1)
      },

      span(
        fontSize := "13px",
        color := "grey",
        marginRight := "0.5rem",

        index + 1,
      ),
      div(
        layoutFlex(),
        justifyContent := "space-between",
        width := "100%",

        span(child.text <-- guessSignal.map(_._1)),
        span(child.text <-- guessSignal.map(_._2)),
      ),
    )
