package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, EventSourceDao, QueueEventDao}
import com.amplify.api.domain.models.{EventSource, QueueEvent}
import com.amplify.api.services.converters.EventSourceConverter.eventSourceToEventSourceDb
import com.amplify.api.services.converters.QueueEventConverter.queueEventToQueueEventDb
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventServiceImpl @Inject()(
    db: DbioRunner,
    eventSourceDao: EventSourceDao,
    queueEventDao: QueueEventDao)(
    implicit ex: ExecutionContext) extends EventService {

  override def create(eventSource: EventSource, queueEvents: QueueEvent*): Future[Unit] = {
    val action =
      for {
        createdEventSource ← eventSourceDao.create(eventSourceToEventSourceDb(eventSource))
        newQueueEvents = queueEvents.map(queueEventToQueueEventDb(createdEventSource, _))
        _ ← queueEventDao.create(createdEventSource, newQueueEvents)
      } yield ()

    db.runTransactionally(action)
  }
}
