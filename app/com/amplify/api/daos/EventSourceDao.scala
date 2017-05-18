package com.amplify.api.daos

import com.amplify.api.daos.models.EventSourceDb
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[EventSourceDaoImpl])
trait EventSourceDao {

  def create(eventSource: EventSourceDb): DBIO[EventSourceDb]
}
