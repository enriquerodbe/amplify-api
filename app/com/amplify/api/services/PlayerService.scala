package com.amplify.api.services

import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue, Queue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[PlayerServiceImpl])
trait PlayerService {

  def play(venue: AuthenticatedVenue, queue: Queue)(implicit authToken: AuthToken): Future[Unit]

  def pause(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit]
}
