package openredactle.server.data

import com.typesafe.scalalogging.Logger

trait ImplicitLazyLogger:
  implicit lazy val logger: Logger = Logger(this.getClass)
