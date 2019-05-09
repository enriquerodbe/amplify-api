package com.amplify.api.shared.daos

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.Future
import slick.jdbc.JdbcProfile

class DbioRunner @Inject()(
    val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def run[R, S <: NoStream, E <: Effect](
      action: DBIOAction[R, S, E]): Future[R] = db.run(action)
}
