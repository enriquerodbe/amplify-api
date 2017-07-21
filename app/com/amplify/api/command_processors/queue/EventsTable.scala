package com.amplify.api.command_processors.queue

import com.amplify.api.command_processors.queue.CommandProcessor.Command
import com.amplify.api.command_processors.queue.EventType.QueueEventType
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.{BaseTable, UsersTable, VenuesTable}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Identifier
import java.time.Instant

trait EventsTable extends BaseTable with VenuesTable with UsersTable {

  import profile.api._

  implicit val queueEventTypeType =
    MappedColumnType.base[QueueEventType, Int](_.id, EventType.apply)

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueEvents(tag: Tag) extends Table[EventDb](tag, "queue_events") {
    def id = column[Id[Event]]("id", O.PrimaryKey, O.AutoInc)
    def queueCommandId = column[Id[Command]]("queue_command_id")
    def eventType = column[QueueEventType]("queue_event_type")
    def contentProvider = column[Option[ContentProviderType]]("content_provider")
    def contentProviderIdentifier = column[Option[Identifier]]("content_identifier")
    def createdAt = column[Instant]("created_at")

    def contentIdentifier =
      (contentProvider, contentProviderIdentifier) <>
        (mapOptionalProviderIdentifier, unmapOptionalProviderIdentifier)

    def * = (id, queueCommandId, eventType, contentIdentifier, createdAt) <>
      (EventDb.tupled, EventDb.unapply)
  }

  lazy val queueEventsTable = TableQuery[QueueEvents]
}
