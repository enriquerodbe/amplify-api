package com.amplify.api.domain.models.primitives

case class Token(value: String) extends AnyVal {

  override def toString: String = value
}

object Token {

  def apply(value: String): Token = {
    require(value != null && value.trim.nonEmpty, "Empty Token")
    new Token(value)
  }
}
