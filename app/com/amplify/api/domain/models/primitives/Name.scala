package com.amplify.api.domain.models.primitives

case class Name(value: String) extends AnyVal {

  override def toString: String = value
}

object Name {

  def apply(value: String): Name = {
    require(value != null && value.trim.nonEmpty, "Empty Name")
    new Name(value)
  }
}
