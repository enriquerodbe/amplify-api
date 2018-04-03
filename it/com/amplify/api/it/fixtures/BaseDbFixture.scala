package com.amplify.api.it.fixtures

import play.api.db.slick.HasDatabaseConfigProvider
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import slick.jdbc.JdbcProfile

trait BaseDbFixture { self: HasDatabaseConfigProvider[JdbcProfile] ⇒

  import profile.api._

  implicit class FutureSpecUtilities[T](future: Future[T]) {

    def await(atMost: Duration = 2.seconds): T = Await.result(future, atMost)
  }

  implicit class DbioSpecUtilities[T](action: DBIO[T]) {

    def await(atMost: Duration = 2.seconds): T = db.run(action).await(atMost)
  }
}
