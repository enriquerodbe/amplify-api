package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthenticatedVenue
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenuePlayerLogicImpl])
trait VenuePlayerLogic {

  def play(venue: AuthenticatedVenue): Future[Unit]

  def pause(venue: AuthenticatedVenue): Future[Unit]

  def startAmplifying(venue: AuthenticatedVenue): Future[Unit]

  def stopAmplifying(venue: AuthenticatedVenue): Future[Unit]

  def trackFinished(venue: AuthenticatedVenue): Future[Unit]
}
