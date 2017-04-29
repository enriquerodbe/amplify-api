package com.amplify.api.utils

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object FutureUtils {

  // scalastyle:off method.name
  implicit class OptionT[T](optional: Option[T]) {

    def ?!(exception: Exception): Future[T] = {
      optional.map(Future.successful).getOrElse(Future.failed(exception))
    }

    def ??(exception: Exception): Try[T] = optional.map(Success(_)).getOrElse(Failure(exception))
  }

  implicit class FutureT[T](optFuture: Future[Option[T]])(implicit ec: ExecutionContext) {

    def ?!(exception: Exception): Future[T] = optFuture.flatMap(_ ?! exception)
  }
}
