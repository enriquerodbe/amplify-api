package com.amplify.api.daos

import com.amplify.api.daos.models.{EventSourceDb, QueueEventDb}
import com.amplify.api.daos.schema.QueueEventsTable
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class QueueEventDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends QueueEventDao with QueueEventsTable {

  import profile.api._

  override def create(eventSource: EventSourceDb, queueEvents: Seq[QueueEventDb]): DBIO[Unit] = {
    (queueEventsTable ++= queueEvents).map(_ ⇒ ())
  }
}
