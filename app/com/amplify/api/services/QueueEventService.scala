package com.amplify.api.services

import com.amplify.api.domain.models.{QueueCommand, QueueEvent}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueEventServiceImpl])
trait QueueEventService {

  def create(queueCommand: QueueCommand, queueEvents: QueueEvent*): Future[Unit]
}
