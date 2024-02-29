package openredactle.webapp

import com.raquo.laminar.api.L.Var

import scala.annotation.targetName

extension [T](v: Var[T])
  @targetName("set")
  def :=(now: T): Unit = v set now
