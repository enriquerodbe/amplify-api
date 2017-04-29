package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Name(value: String) extends AnyVal {

  override def toString: String = value
}

object Name {

  implicit def nameToString(name: Name): String = name.value

  implicit def stringToName(value: String): Name = Name(value)
}
