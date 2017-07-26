package com.amplify.api.aggregates.queue

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.{ExecutionContext, Future}

class CommandDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends CommandDao with CommandsTable {

  import profile.api._

  private def createAction(queueCommand: CommandDb): DBIO[CommandDb] = {
    val projection = queueCommandsTable returning queueCommandsTable.map(_.id)
    projection into ((obj, id) ⇒ obj.copy(id = id)) += queueCommand
  }

  override def create(queueCommand: CommandDb): Future[CommandDb] = {
    db.run(createAction(queueCommand))
  }
}
