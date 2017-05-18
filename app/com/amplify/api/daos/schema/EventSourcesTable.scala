package com.amplify.api.daos.schema

import com.amplify.api.daos.models.EventSourceDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.EventSourceType.EventSourceType
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.domain.models.{EventSource, User, Venue}
import java.time.Instant

trait EventSourcesTable extends BaseTable with VenuesTable with UsersTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class EventSources(tag: Tag) extends Table[EventSourceDb](tag, "event_sources") {
    def id = column[Id[EventSource]]("id", O.PrimaryKey, O.AutoInc)
    def venueId = column[Id[Venue]]("venue_id")
    def userId = column[Option[Id[User]]]("user_id")
    def eventType = column[EventSourceType]("event_type")
    def contentProvider = column[Option[ContentProviderType]]("content_provider")
    def contentProviderIdentifier = column[Option[Identifier]]("content_identifier")
    def createdAt = column[Instant]("created_at")

    def contentIdentifier =
      (contentProvider, contentProviderIdentifier) <>
        (mapOptionalProviderIdentifier, unmapOptionalProviderIdentifier)

    def venue = foreignKey("venue_fk", venueId, venuesTable)(_.id)
    def user = foreignKey("user_fk", userId, usersTable)(_.id.?)

    def * = (id, venueId, userId, eventType, contentIdentifier, createdAt) <>
      (EventSourceDb.tupled, EventSourceDb.unapply)
  }

  lazy val eventSourcesTable = TableQuery[EventSources]
}
