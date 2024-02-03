package openredactle.server

import openredactle.server.games.Games

import scala.concurrent.ExecutionContext.Implicits.global

@main def main(): Unit =
  Games.runGameReaper()
  WsServer.start()
