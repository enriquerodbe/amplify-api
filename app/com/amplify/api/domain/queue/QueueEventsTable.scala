package com.amplify.api.domain.queue

import com.amplify.api.domain.models.ContentIdentifier
import com.amplify.api.domain.models.primitives.{Code, Id, Uid}
import com.amplify.api.domain.queue.QueueEventType.QueueEventType
import com.amplify.api.shared.daos.BaseTable

private trait QueueEventsTable extends BaseTable {

  import profile.api._

  implicit val queueEventTypeType = {
    MappedColumnType.base[QueueEventType, String](_.toString, QueueEventType.withName)
  }

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueEvents(tag: Tag) extends Table[QueueEvent](tag, "queue_events") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def venueUid = column[Uid]("venue_uid")
    def eventType = column[QueueEventType]("event_type")
    def coinCode = column[Option[Code]]("coin_code")
    def contentIdentifier = column[Option[ContentIdentifier]]("content_identifier")

    def * =
      (venueUid, eventType, coinCode, contentIdentifier) <>
        ((QueueEvent.apply _).tupled, QueueEvent.toTuple)
  }

  lazy val queueEventsTable = TableQuery[QueueEvents]
}
