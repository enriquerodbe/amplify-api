package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[QueueEventDaoImpl])
trait QueueEventDao {

  def create(event: QueueEvent): DBIO[Unit]

  def retrieve(venueUid: Uid): DBIO[Seq[QueueEvent]]
}
