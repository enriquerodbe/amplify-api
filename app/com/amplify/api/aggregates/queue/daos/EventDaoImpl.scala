package com.amplify.api.aggregates.queue.daos

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class EventDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends EventDao with EventsTable {

  import profile.api._

  override def create(queueEvent: Seq[EventDb]): DBIO[Unit] = {
    (insertEventQuery ++= queueEvent).map(_ ⇒ ())
  }
}
