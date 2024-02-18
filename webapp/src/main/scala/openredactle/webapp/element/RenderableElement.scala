package openredactle.webapp.element

import com.raquo.laminar.api.L.Element

import scala.language.implicitConversions

trait RenderableElement:
  def renderElement: Element

implicit def renderableElementToElement(renderableElement: RenderableElement): Element =
  renderableElement.renderElement
