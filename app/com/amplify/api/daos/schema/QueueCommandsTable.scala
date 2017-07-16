package com.amplify.api.daos.schema

import com.amplify.api.daos.models.QueueCommandDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.QueueCommandType.QueueCommandType
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.domain.models.{QueueCommand, User, Venue}
import java.time.Instant

trait QueueCommandsTable extends BaseTable with VenuesTable with UsersTable {

  import profile.api._

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueCommands(tag: Tag) extends Table[QueueCommandDb](tag, "queue_commands") {
    def id = column[Id[QueueCommand]]("id", O.PrimaryKey, O.AutoInc)
    def venueId = column[Id[Venue]]("venue_id")
    def userId = column[Option[Id[User]]]("user_id")
    def queueCommandType = column[QueueCommandType]("queue_command_type")
    def contentProvider = column[Option[ContentProviderType]]("content_provider")
    def contentProviderIdentifier = column[Option[Identifier]]("content_identifier")
    def createdAt = column[Instant]("created_at")

    def contentIdentifier =
      (contentProvider, contentProviderIdentifier) <>
        (mapOptionalProviderIdentifier, unmapOptionalProviderIdentifier)

    def venue = foreignKey("venue_fk", venueId, venuesTable)(_.id)
    def user = foreignKey("user_fk", userId, usersTable)(_.id.?)

    def * = (id, venueId, userId, queueCommandType, contentIdentifier, createdAt) <>
      (QueueCommandDb.tupled, QueueCommandDb.unapply)
  }

  lazy val queueCommandsTable = TableQuery[QueueCommands]
}
