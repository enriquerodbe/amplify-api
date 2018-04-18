package com.amplify.api.domain.models.primitives

final case class Id private (value: Long) extends AnyVal {

  override def toString: String = value.toString
}

object Id {

  def apply(value: Long): Id = {
    require(value >= 0, "Id must be positive or zero")
    new Id(value)
  }
}
