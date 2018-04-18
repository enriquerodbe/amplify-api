package com.amplify.api.domain.models.primitives

final case class Identifier private (value: String) extends AnyVal {

  override def toString: String = value
}

object Identifier {

  def apply(value: String): Identifier = {
    require(value != null && value.trim.nonEmpty, "Empty identifier")
    new Identifier(value)
  }
}
