package com.amplify.api.services

import com.amplify.api.domain.models.{AuthenticatedVenue, Queue, QueueEvent, UnauthenticatedVenue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def retrieve(venue: AuthenticatedVenue): Future[Queue]

  def update(venue: UnauthenticatedVenue, events: QueueEvent*): Future[Queue]
}
