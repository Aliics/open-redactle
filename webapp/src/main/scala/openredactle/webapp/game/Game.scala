package openredactle.webapp.game

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.message
import openredactle.shared.message.Message
import openredactle.webapp.connectWs
import upickle.default.write

object Game:
  val gameId: Var[Option[String]] = Var(None)

  private val ws = connectWs()

  def addGuess(guess: String): Unit =
    ws.send(write(Message.AddGuess(gameId.now().get, guess)))

  def renderElement: Element =
    div(
      display := "flex",
      flexDirection := "column",
      height := "100vh",
      maxHeight := "100vh",

      div(
        display := "flex",
        maxHeight := "calc(100% - 2rem)", // Sort of a hack for the status bar.
        flexGrow := "1",

        Article.renderElement,
        Guesses.renderElement,
      ),

      StatusBar.renderElement,
    )
