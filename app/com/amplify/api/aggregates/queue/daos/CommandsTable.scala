package com.amplify.api.aggregates.queue.daos

import com.amplify.api.daos.schema.BaseTable
import com.amplify.api.domain.models.primitives.Id
import java.time.Instant
import play.api.libs.json.Json

trait CommandsTable extends BaseTable {

  import profile.api._

  private implicit val commandDbDataColumnType = MappedColumnType.base[CommandDbData, String](
    v ⇒ Json.stringify(CommandDbDataSerializer.serialize(v)),
    v ⇒ CommandDbDataSerializer.deserialize(Json.parse(v))
  )

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueCommands(tag: Tag) extends Table[CommandDb](tag, "queue_commands") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def venueId = column[Id]("venue_id")
    def data = column[CommandDbData]("data")
    def createdAt = column[Instant]("created_at")

    def * = (id, venueId, data, createdAt) <> (CommandDb.tupled, CommandDb.unapply)
  }

  lazy val queueCommandsTable = TableQuery[QueueCommands]

  lazy val insertCommandQuery =
    queueCommandsTable returning queueCommandsTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))
}
