package com.amplify.api.services

import com.amplify.api.domain.models.{Queue, QueueEvent, UnauthenticatedVenue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def retrieve(venue: UnauthenticatedVenue): Future[Queue]

  def update(venue: UnauthenticatedVenue, events: QueueEvent*): Future[Queue]
}
