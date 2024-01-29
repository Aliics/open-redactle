package openredactle.webapp

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import openredactle.shared.message.Message
import org.scalajs.dom.WebSocket
import upickle.default.{*, given}

object Game:
  private val ws = WebSocket("ws://localhost:8080/", "ws")
  ws.onopen = _ =>
    ws.send(write(Message.StartGame()))

  ws.onmessage = msg =>
    println(msg.data)

  def renderElement: Element =
    div(
      display := "flex",
      height := "100%",

      Article.renderElement,
      Guesses.renderElement,
    )
