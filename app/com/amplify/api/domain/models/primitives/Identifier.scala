package com.amplify.api.domain.models.primitives

case class Identifier(value: String) extends AnyVal {

  override def toString: String = value
}

object Identifier {

  def apply(value: String): Identifier = {
    require(value != null && value.trim.nonEmpty, "Empty identifier")
    new Identifier(value)
  }
}
