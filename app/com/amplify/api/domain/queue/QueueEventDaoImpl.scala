package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.Uid
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

private class QueueEventDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext) extends QueueEventDao with QueueEventsTable {

  import profile.api._

  override def create(event: QueueEvent): DBIO[Unit] = {
    (queueEventsTable += event).map(_ ⇒ ())
  }

  override def retrieve(venueUid: Uid): DBIO[Seq[QueueEvent]] = {
    queueEventsTable.filter(_.venueUid === venueUid).sortBy(_.id.asc).result
  }
}
