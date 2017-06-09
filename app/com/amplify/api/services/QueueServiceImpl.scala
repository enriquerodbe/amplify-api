package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class QueueServiceImpl extends QueueService {

  private var queues: Map[Uid, Queue] = Map.empty

  override def retrieve(venue: UnauthenticatedVenue): Future[Queue] = {
    Future.successful(queues.getOrElse(venue.uid, Queue()))
  }

  override def update(venue: UnauthenticatedVenue, events: QueueEvent*): Future[Queue] = {
    val queue = queues.getOrElse(venue.uid, Queue())
    val result = events.foldRight(queue)(_ process _)
    queues = queues.updated(venue.uid, result)
    Future.successful(result)
  }
}
