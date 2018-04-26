package com.amplify.api.utils

import scala.concurrent.{ExecutionContext, Future}

object FutureUtils {

  // scalastyle:off method.name
  implicit class FutureT[T](optFuture: Future[Option[T]])(implicit ec: ExecutionContext) {

    def ?!(exception: Exception): Future[T] = {
      optFuture.flatMap(_.map(Future.successful).getOrElse(Future.failed(exception)))
    }
  }
}
