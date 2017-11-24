package com.amplify.api.aggregates.queue.daos

import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[EventDaoImpl])
trait EventDao {

  def create(queueEvents: Seq[EventDb]): Future[Unit]
}
