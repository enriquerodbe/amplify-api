package com.amplify.api.domain.models.primitives

case class Token(value: String) extends AnyVal {

  override def toString: String = value
}
