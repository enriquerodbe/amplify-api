package com.amplify.api.aggregates.queue.daos

import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[EventDaoImpl])
trait EventDao {

  def create(queueEvents: Seq[EventDb]): DBIO[Unit]
}
