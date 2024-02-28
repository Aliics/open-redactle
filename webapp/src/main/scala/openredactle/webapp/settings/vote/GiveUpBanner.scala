package openredactle.webapp.settings.vote

import com.raquo.laminar.api.L.{*, given}
import openredactle.webapp.element.RenderableElement

object GiveUpBanner extends RenderableElement:
  val showing: Var[Boolean] = Var(true)
  
  override def renderElement: Element =
    div()
