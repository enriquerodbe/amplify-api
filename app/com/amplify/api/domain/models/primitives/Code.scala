package com.amplify.api.domain.models.primitives

final case class Code(value: String) extends AnyVal {

  override def toString: String = value
}

object Code {

  def apply(value: String): Code = {
    require(value != null && value.trim.nonEmpty, "Empty code")
    new Code(value)
  }
}
