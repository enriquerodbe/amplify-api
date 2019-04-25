package com.amplify.api.domain.models.primitives

import scala.util.Random

final case class Code private(value: String) extends AnyVal {

  override def toString: String = value
}

object Code {

  val DEFAULT_LENGTH = 4

  def generate(length: Int = DEFAULT_LENGTH): Code = {
    Code(Random.alphanumeric.take(DEFAULT_LENGTH).mkString)
  }

  def apply(value: String): Code = {
    require(value != null && value.trim.nonEmpty, "Empty code")
    new Code(value)
  }
}
