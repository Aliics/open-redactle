package openredactle.server

import openredactle.server.games.Games

import scala.concurrent.ExecutionContext.Implicits.global

@main def main(): Unit =
  sys.addShutdownHook:
    WsServer.stop()

  Games.runGameReaper()
  WsServer.start()
