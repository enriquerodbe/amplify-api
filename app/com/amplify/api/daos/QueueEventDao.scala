package com.amplify.api.daos

import com.amplify.api.daos.models.QueueEventDb
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[QueueEventDaoImpl])
trait QueueEventDao {

  def create(queueEvents: Seq[QueueEventDb]): DBIO[Unit]
}
