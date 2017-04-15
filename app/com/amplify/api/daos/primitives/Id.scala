package com.amplify.api.daos.primitives

import scala.language.implicitConversions
import slick.lifted.MappedTo

final case class Id[T](value: Long) extends AnyVal with MappedTo[Long] {

  override def toString: String = value.toString
}

object Id {

  implicit def idToLong[T](id: Id[T]): Long = id.value

  implicit def longToId[T](value: Long): Id[T] = Id[T](value)
}
