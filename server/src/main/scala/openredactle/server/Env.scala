package openredactle.server

import scala.util.CommandLineParser.FromString

enum Env(
  val port: Int,
  val emitMetrics: Boolean,
  val metricsPrefix: String
):
  case Local extends Env(
    port = 8080,
    emitMetrics = false,
    metricsPrefix = "local",
  )
  case Prod extends Env(
    port = 8080,
    emitMetrics = true,
    metricsPrefix = "prod",
  )

object Env:
  given FromString[Env] with
    def fromString(s: String): Env = Env valueOf s
