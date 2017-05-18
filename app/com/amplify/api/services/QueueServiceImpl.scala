package com.amplify.api.services

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models._
import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class QueueServiceImpl extends QueueService {

  private var queues: Map[Id[Venue], Queue] = Map.empty

  override def retrieve(venue: AuthenticatedVenue): Future[Queue] = {
    Future.successful(queues.getOrElse(venue.id, Queue()))
  }

  override def update(venue: UnauthenticatedVenue, events: QueueEvent*): Future[Queue] = {
    val queue = queues.getOrElse(venue.id, Queue())
    val result = events.foldRight(queue)(_ process _)
    queues = queues.updated(venue.id, result)
    Future.successful(result)
  }
}
