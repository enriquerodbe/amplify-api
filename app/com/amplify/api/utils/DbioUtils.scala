package com.amplify.api.utils

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import slick.dbio.DBIO

object DbioUtils {

  // scalastyle:off method.name
  implicit class DbioT[T](optDbio: DBIO[Option[T]])(implicit ec: ExecutionContext) {

    def ?!(exception: Exception): DBIO[T] = {
      optDbio.flatMap(_.map(DBIO.successful).getOrElse(DBIO.failed(exception)))
    }
  }
}
