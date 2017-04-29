package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Identifier(value: String) extends AnyVal {

  override def toString: String = value
}

object Identifier {

  implicit def identifierToLong(identifier: Identifier): String = identifier.value

  implicit def longToIdentifier(value: String): Identifier = Identifier(value)
}
