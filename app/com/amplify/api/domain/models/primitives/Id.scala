package com.amplify.api.domain.models.primitives

final case class Id(value: Long) extends AnyVal {

  override def toString: String = value.toString
}
