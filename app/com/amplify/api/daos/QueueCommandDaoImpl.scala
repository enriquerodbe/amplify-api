package com.amplify.api.daos

import com.amplify.api.daos.models.QueueCommandDb
import com.amplify.api.daos.schema.QueueCommandsTable
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class QueueCommandDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends QueueCommandDao with QueueCommandsTable {

  import profile.api._

  override def create(queueCommand: QueueCommandDb): DBIO[QueueCommandDb] = {
    val projection = queueCommandsTable returning queueCommandsTable.map(_.id)
    projection into ((obj, id) ⇒ obj.copy(id = id)) += queueCommand
  }
}
