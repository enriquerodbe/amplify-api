package com.amplify.api.command_processors.queue

import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[EventDaoImpl])
trait EventDao {

  def create(queueEvents: Seq[EventDb]): Future[Unit]
}
