package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Identifier(value: String) extends AnyVal {

  override def toString: String = value
}

object Identifier {

  implicit def identifierToString(identifier: Identifier): String = identifier.value

  implicit def stringToIdentifier(value: String): Identifier = Identifier(value)
}
