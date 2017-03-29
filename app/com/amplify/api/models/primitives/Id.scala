package com.amplify.api.models.primitives

import scala.language.implicitConversions
import slick.lifted.MappedTo

final case class Id[T] private (value: Long) extends AnyVal with MappedTo[Long]

object Id {

  implicit def idToLong[T](id: Id[T]): Long = id.value

  implicit def longToId[T](value: Long): Id[T] = Id[T](value)
}
