package com.amplify.api.services

import com.amplify.api.domain.models.{EventSource, QueueEvent}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[EventServiceImpl])
trait EventService {

  def create(eventSource: EventSource, queueEvents: QueueEvent*): Future[Unit]
}
