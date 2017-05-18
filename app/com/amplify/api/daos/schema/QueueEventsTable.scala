package com.amplify.api.daos.schema

import com.amplify.api.daos.models.QueueEventDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.QueueEventType.QueueEventType
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.domain.models.{EventSource, QueueEvent, User, Venue}
import java.time.Instant

trait QueueEventsTable extends BaseTable with VenuesTable with UsersTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueEvents(tag: Tag) extends Table[QueueEventDb](tag, "queue_events") {
    def id = column[Id[QueueEvent]]("id", O.PrimaryKey, O.AutoInc)
    def eventSourceId = column[Id[EventSource]]("event_source_id")
    def eventType = column[QueueEventType]("event_type")
    def contentProvider = column[Option[ContentProviderType]]("content_provider")
    def contentProviderIdentifier = column[Option[Identifier]]("content_identifier")
    def createdAt = column[Instant]("created_at")

    def contentIdentifier =
      (contentProvider, contentProviderIdentifier) <>
        (mapOptionalProviderIdentifier, unmapOptionalProviderIdentifier)

    def * = (id, eventSourceId, eventType, contentIdentifier, createdAt) <>
      (QueueEventDb.tupled, QueueEventDb.unapply)
  }

  lazy val queueEventsTable = TableQuery[QueueEvents]
}
