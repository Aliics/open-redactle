package openredactle.server

import openredactle.server.games.Games

import scala.concurrent.ExecutionContext.Implicits.global

@main def main(env: Env): Unit =
  given Env = env
  
  val games = Games()
  val ws = WsServer(games)
  
  sys.addShutdownHook:
    ws.stop()

  games.runGameReaper()
  ws.start()
