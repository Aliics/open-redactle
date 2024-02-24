package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.*
import openredactle.webapp.element.RenderableElement

object Guesses extends RenderableElement:
  private type Guess = (String, String, Int, Boolean)

  val guessedWords: Var[List[Guess]] = Var(List())

  override lazy val renderElement: Element =
    div(
      width := "26rem",
      height := "100%",
      layoutFlex("column"),
      borderLeft := solidBorder(),

      renderGuessForm,

      div(
        borderBottom := solidBorder(),
        fontSize := "15px",
        padding := "0.2rem 1rem",
        layoutFlex(),
        columnGap := "1rem",

        children <-- guessedWords.signal.map: guessedWords =>
          span(s"Guesses: ${guessedWords.size}") ::
            span(s"Correct: ${guessedWords.count(w => w._3 > 0 && !w._4)}") ::
            Nil
      ),

      div(
        flexGrow := 1,
        overflowY := "scroll",

        children <-- guessedWords.signal
          .splitByIndex(renderGuess)
          .map(_.reverse),
      ),
    )

  private def renderGuessForm =
    val guessInput = Var("")

    form(
      layoutFlex(),
      padding := "1rem",
      borderBottom := solidBorder(),

      onSubmit.preventDefault.mapTo(guessInput.now()) --> { w =>
        Game.addGuess(w)
        guessInput.set("")
      },

      input(
        flexGrow := 1,
        minWidth := "16rem",

        placeholder := "Guess a word",

        value <-- guessInput,
        onInput.mapToValue --> guessInput,
      ),
      button(
        colored(),
        "Guess",
      ),
    )

  private def renderGuess(index: Int, initial: Guess, guessSignal: Signal[Guess]) =
    val (playerId, guessed, matched, isHint) = initial
    val isCorrectGuess = matched > 0
    div(
      layoutFlex(),
      alignItems := "center",
      borderBottom := solidBorder(),
      minWidth := "fit-content",
      fontSize := "16px",
      padding := "0.25rem",
      cursor := (if isCorrectGuess then "pointer" else "default"),
      userSelect := "none",

      backgroundColor <--
        Article.selectedGuess.signal.map:
          case Some(sel -> _) if sel == guessed =>
            Colors.highlightColor(isHint)
          case _ =>
            Colors.wordColor(isCorrectGuess, isHint)
            ,

        onClick --> { _ =>
        if isCorrectGuess then
          Article.selectedGuess.update:
            case Some(sel -> _) if sel == guessed => None
            case _ => Some(guessed -> isHint)
      },

      span(
        fontSize := "15px",
        marginRight := "0.5rem",

        child.text <-- Game.playerEmojis.signal.map(_.get(playerId).fold("?")(_.code)),
      ),
      div(
        layoutFlex(),
        justifyContent := "space-between",
        width := "100%",

        span(child.text <-- guessSignal.map(_._2)),
        span(child.text <-- guessSignal.map(_._3)),
      ),
    )
