package com.amplify.api.services

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{Queue, QueueEvent, UnauthenticatedVenue, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def retrieve(venueId: Id[Venue]): Future[Queue]

  def update(venue: UnauthenticatedVenue, events: QueueEvent*): Future[Queue]
}
