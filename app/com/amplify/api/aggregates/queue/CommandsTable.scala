package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.CommandType.QueueCommandType
import com.amplify.api.daos.schema.BaseTable
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.{Id, Identifier}
import java.time.Instant

trait CommandsTable extends BaseTable {

  import profile.api._

  implicit val queueCommandTypeType =
    MappedColumnType.base[QueueCommandType, Int](_.id, CommandType.apply)

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueCommands(tag: Tag) extends Table[CommandDb](tag, "queue_commands") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def venueId = column[Id]("venue_id")
    def userId = column[Option[Id]]("user_id")
    def queueCommandType = column[QueueCommandType]("queue_command_type")
    def contentProvider = column[Option[ContentProviderType]]("content_provider")
    def contentProviderIdentifier = column[Option[Identifier]]("content_identifier")
    def createdAt = column[Instant]("created_at")

    def contentIdentifier =
      (contentProvider, contentProviderIdentifier) <>
        (mapOptionalProviderIdentifier, unmapOptionalProviderIdentifier)

    def * = (id, venueId, userId, queueCommandType, contentIdentifier, createdAt) <>
      (CommandDb.tupled, CommandDb.unapply)
  }

  lazy val queueCommandsTable = TableQuery[QueueCommands]
}
