package com.amplify.api.daos

import com.amplify.api.daos.models.{EventSourceDb, QueueEventDb}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[QueueEventDaoImpl])
trait QueueEventDao {

  def create(eventSource: EventSourceDb, queueEvents: Seq[QueueEventDb]): DBIO[Unit]
}
