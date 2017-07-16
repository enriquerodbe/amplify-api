package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, QueueCommandDao, QueueEventDao}
import com.amplify.api.domain.models.{QueueCommand, QueueEvent}
import com.amplify.api.services.converters.QueueCommandConverter.queueCommandToQueueCommandDb
import com.amplify.api.services.converters.QueueEventConverter.queueEventToQueueEventDb
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class QueueEventServiceImpl @Inject()(
    db: DbioRunner,
    queueCommandDao: QueueCommandDao,
    queueEventDao: QueueEventDao)(
    implicit ex: ExecutionContext) extends QueueEventService {

  override def create(queueCommand: QueueCommand, queueEvents: QueueEvent*): Future[Unit] = {
    val action =
      for {
        createdQueueCommand ← queueCommandDao.create(queueCommandToQueueCommandDb(queueCommand))
        newQueueEvents = queueEvents.map(queueEventToQueueEventDb(createdQueueCommand, _))
        _ ← queueEventDao.create(newQueueEvents)
      }
      yield ()

    db.runTransactionally(action)
  }
}
