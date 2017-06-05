package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenuePlayerLogicImpl])
trait VenuePlayerLogic {

  def play(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit]

  def pause(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit]

  def startAmplifying(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit]

  def stopAmplifying(venue: AuthenticatedVenue)(implicit authToken: AuthToken): Future[Unit]

  def trackFinished(venue: AuthenticatedVenue): Future[Unit]
}
