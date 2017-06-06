package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions
import scala.util.Random

case class Uid(value: String) extends AnyVal {

  override def toString: String = value
}

object Uid {

  val DEFAULT_LENGTH = 8

  def apply(): Uid = Uid(Random.alphanumeric.take(DEFAULT_LENGTH).mkString(""))

  implicit def uidToString(uid: Uid): String = uid.value

  implicit def stringToUid(value: String): Uid = Uid(value)
}
