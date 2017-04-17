package com.amplify.api.daos

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile

class DbioRunner @Inject()(
    val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def runTransactionally[R, S <: NoStream, E <: Effect](
      action: DBIOAction[R, S, E])(
      implicit ec: ExecutionContext): Future[R] = run(action.transactionally)

  def run[R, S <: NoStream, E <: Effect](
      action: DBIOAction[R, S, E])(
      implicit ec: ExecutionContext): Future[R] = db.run(action)
}