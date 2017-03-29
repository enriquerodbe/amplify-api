package com.amplify.api.it

import play.api.db.slick.HasDatabaseConfigProvider
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import slick.relational.RelationalProfile

trait BaseFixture { self: HasDatabaseConfigProvider[RelationalProfile] =>

  import profile.api._

  implicit class FutureSpecUtilities[T](future: Future[T]) {

    def await(atMost: Duration = 2.seconds): T = Await.result(future, atMost)
  }

  implicit class DbioSpecUtilities[T](action: DBIO[T]) {

    def await(atMost: Duration = 2.seconds): T = db.run(action).await(atMost)
  }
}
