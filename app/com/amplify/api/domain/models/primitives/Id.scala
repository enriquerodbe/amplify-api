package com.amplify.api.domain.models.primitives

import scala.language.implicitConversions

final case class Id(value: Long) extends AnyVal {

  override def toString: String = value.toString
}

object Id {

  implicit def idToLong(id: Id): Long = id.value

  implicit def longToId(value: Long): Id = Id(value)
}
