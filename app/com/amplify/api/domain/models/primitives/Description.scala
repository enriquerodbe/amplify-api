package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Description(value: String) extends AnyVal {

  override def toString: String = value
}

object Description {

  implicit def descriptionToString(description: Description): String = description.value

  implicit def stringToDescription(value: String): Description = Description(value)
}
