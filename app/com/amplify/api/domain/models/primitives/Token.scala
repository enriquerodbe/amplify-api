package com.amplify.api.domain.models.primitives

final case class Token[T <: TokenType] private (value: String) extends AnyVal {

  override def toString: String = value
}

sealed trait TokenType
sealed trait Refresh extends TokenType
sealed trait Access extends TokenType
sealed trait AuthorizationCode extends TokenType

object Token {

  def apply[T <: TokenType](value: String): Token[T] = {
    require(value != null && value.trim.nonEmpty, "Empty Token")
    new Token[T](value)
  }
}
