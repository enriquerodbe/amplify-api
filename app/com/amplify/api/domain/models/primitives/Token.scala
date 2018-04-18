package com.amplify.api.domain.models.primitives

final case class Token private (value: String) extends AnyVal {

  override def toString: String = value
}

object Token {

  def apply(value: String): Token = {
    require(value != null && value.trim.nonEmpty, "Empty Token")
    new Token(value)
  }
}
