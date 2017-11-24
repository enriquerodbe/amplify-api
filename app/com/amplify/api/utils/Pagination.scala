package com.amplify.api.utils

import com.amplify.api.domain.models.AuthToken
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, __}
import scala.concurrent.Future

trait Pagination { self: OAuthClient ⇒

  val itemsField: String
  val totalField: String
  val nextField: String
  val paginationOffsetHeader: String

  case class Page[T](items: Seq[T], total: Int, next: Option[String])

  implicit def pageFormat[T: Format]: Format[Page[T]] = {
    ((__ \ itemsField).format[Seq[T]] ~
      (__ \ totalField).format[Int] ~
      (__ \ nextField).formatNullable[String])(Page.apply, unlift(Page.unapply[T]))
  }

  def paginatedFetch[T: Format](
      path: String,
      query: Map[String, String],
      acc: Seq[T],
      offset: Int)(
      implicit token: AuthToken): Future[Seq[T]] = {
    fetchPage[T](path, query, offset).flatMap { page ⇒
      val items = acc ++ page.items
      page.next match {
        case Some(_) ⇒ paginatedFetch(path, query, items, offset + page.total)
        case _ ⇒ Future.successful(items)
      }
    }
  }

  def fetchPage[T: Format](
      path: String,
      query: Map[String, String],
      offset: Int)(
      implicit token: AuthToken): Future[Page[T]] = {
    apiGet[Page[T]](path, query.updated(paginationOffsetHeader, offset.toString))
  }
}
