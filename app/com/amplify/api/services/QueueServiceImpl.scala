package com.amplify.api.services

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models._
import javax.inject.Singleton
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class QueueServiceImpl extends QueueService {

  private var queues: Map[Id[Venue], Queue] = Map.empty

  override def retrieve(venue: UnauthenticatedVenue): Future[Queue] = {
    Future.successful(queues.getOrElse(venue.id, Queue()))
  }

  override def update(venue: UnauthenticatedVenue, events: QueueEvent*): Future[Queue] = {
    val queue = queues.getOrElse(venue.id, Queue())
    val result = events.foldLeft(Try(queue))(_ flatMap _.process)
    result match {
      case Success(q) ⇒
        queues = queues.updated(venue.id, q)
        Future.successful(q)
      case Failure(ex) ⇒
        Future.failed(ex)
    }
  }
}
