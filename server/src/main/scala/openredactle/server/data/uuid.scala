package openredactle.server.data

import java.util.UUID
import scala.language.implicitConversions

implicit def uuidToString(uuid: UUID): String = uuid.toString
