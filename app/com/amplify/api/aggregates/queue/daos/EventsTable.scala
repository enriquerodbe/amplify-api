package com.amplify.api.aggregates.queue.daos

import com.amplify.api.aggregates.queue.EventType
import com.amplify.api.aggregates.queue.EventType.QueueEventType
import com.amplify.api.daos.schema.BaseTable
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Id, Identifier}
import java.time.Instant

trait EventsTable extends BaseTable {

  import profile.api._

  implicit val queueEventTypeType =
    MappedColumnType.base[QueueEventType, Int](_.id, EventType.apply)

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueEvents(tag: Tag) extends Table[EventDb](tag, "queue_events") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def queueCommandId = column[Id]("queue_command_id")
    def eventType = column[QueueEventType]("queue_event_type")
    def contentProvider = column[Option[ContentProviderType]]("content_provider")
    def contentProviderIdentifier = column[Option[Identifier]]("content_identifier")
    def createdAt = column[Instant]("created_at")

    def contentIdentifier =
      (contentProvider, contentProviderIdentifier) <>
        (mapOptionalContentProviderIdentifier, unmapOptionalContentProviderIdentifier)

    def * = (id, queueCommandId, eventType, contentIdentifier, createdAt) <>
      (EventDb.tupled, EventDb.unapply)
  }

  lazy val queueEventsTable = TableQuery[QueueEvents]
}
