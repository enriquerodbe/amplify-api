package com.amplify.api.utils

import scala.concurrent.{ExecutionContext, Future}

object FutureUtils {

  // scalastyle:off method.name
  implicit class OptionT[T](optional: Option[T]) {

    def ?!(exception: Exception): Future[T] = optional match {
      case Some(r) ⇒ Future.successful(r)
      case _ ⇒ Future.failed(exception)
    }
  }

  implicit class FutureT[T](optFuture: Future[Option[T]])(implicit ec: ExecutionContext) {

    def ?!(exception: Exception): Future[T] = optFuture.flatMap(_ ?! exception)
  }
}
