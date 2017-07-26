package com.amplify.api.aggregates.queue

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.{ExecutionContext, Future}

class EventDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends EventDao with EventsTable {

  import profile.api._

  private def createAction(queueEvent: Seq[EventDb]): DBIO[Unit] = {
    (queueEventsTable ++= queueEvent).map(_ ⇒ ())
  }

  override def create(queueEvents: Seq[EventDb]): Future[Unit] = db.run(createAction(queueEvents))
}
