package com.amplify.api.domain.models.primitives

import scala.util.Random

case class Uid(value: String) extends AnyVal {

  override def toString: String = value
}

object Uid {

  val DEFAULT_LENGTH = 8

  def apply(): Uid = Uid(Random.alphanumeric.take(DEFAULT_LENGTH).mkString(""))

  def apply(value: String): Uid = {
    require(value != null && value.trim.nonEmpty, "Empty Uid")
    new Uid(value)
  }
}
