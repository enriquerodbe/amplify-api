package com.amplify.api.daos

import com.amplify.api.daos.models.EventSourceDb
import com.amplify.api.daos.schema.EventSourcesTable
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class EventSourceDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends EventSourceDao with EventSourcesTable {

  import profile.api._

  override def create(eventSource: EventSourceDb): DBIO[EventSourceDb] = {
    eventSourcesTable returning eventSourcesTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id)) +=
      eventSource
  }
}
