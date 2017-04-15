package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Name[T](value: String) extends AnyVal {

  override def toString: String = value
}

object Name {

  implicit def nameToString[T](name: Name[T]): String = name.value

  implicit def stringToName[T](value: String): Name[T] = Name[T](value)
}
