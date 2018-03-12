package com.amplify.api.aggregates.queue.daos

import com.amplify.api.daos.schema.BaseTable
import com.amplify.api.domain.models.primitives.Id
import java.time.Instant
import play.api.libs.json.Json

trait EventsTable extends BaseTable {

  import profile.api._

  private implicit val eventDbDataColumnType = MappedColumnType.base[EventDbData, String](
    v ⇒ Json.stringify(EventDbDataSerializer.serialize(v)),
    v ⇒ EventDbDataSerializer.deserialize(Json.parse(v))
  )

  // scalastyle:off public.methods.have.type
  // scalastyle:off method.name
  class QueueEvents(tag: Tag) extends Table[EventDb](tag, "queue_events") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def queueCommandId = column[Id]("queue_command_id")
    def data = column[EventDbData]("data")
    def createdAt = column[Instant]("created_at")

    def * = (id, queueCommandId, data, createdAt) <> (EventDb.tupled, EventDb.unapply)
  }

  lazy val queueEventsTable = TableQuery[QueueEvents]

  lazy val insertEventQuery =
    queueEventsTable returning queueEventsTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))
}
