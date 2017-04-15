package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Identifier[T](value: String) extends AnyVal {

  override def toString: String = value
}

object Identifier {

  implicit def identifierToLong[T](identifier: Identifier[T]): String = identifier.value

  implicit def longToIdentifier[T](value: String): Identifier[T] = Identifier[T](value)
}
