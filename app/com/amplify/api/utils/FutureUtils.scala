package com.amplify.api.utils

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import slick.dbio.DBIO

object FutureUtils {

  // scalastyle:off method.name
  implicit class OptionT[T](optional: Option[T]) {

    def ?!(exception: Exception): Future[T] = {
      optional.map(Future.successful).getOrElse(Future.failed(exception))
    }

    def ??(exception: Exception): Try[T] = optional.map(Success(_)).getOrElse(Failure(exception))

    def ?&(exception: Exception): DBIO[T] = {
      optional.map(DBIO.successful).getOrElse(DBIO.failed(exception))
    }
  }

  implicit class FutureT[T](optFuture: Future[Option[T]])(implicit ec: ExecutionContext) {

    def ?!(exception: Exception): Future[T] = optFuture.flatMap(_ ?! exception)
  }

  implicit class DbioT[T](optDbio: DBIO[Option[T]])(implicit ec: ExecutionContext) {

    def ?!(exception: Exception): DBIO[T] = optDbio.flatMap(_ ?& exception)
  }
}
