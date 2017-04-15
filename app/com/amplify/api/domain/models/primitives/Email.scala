package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

case class Email(value: String) extends AnyVal {

  override def toString: String = value
}

object Email {

  implicit def emailToString(email: Email): String = email.value

  implicit def stringToEmail(value: String): Email = Email(value)
}
