package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthLogicImpl])
trait VenueAuthLogic {

  def signUp(authorizationCode: AuthToken): Future[Venue]

  def login(authToken: AuthToken): Future[Option[Venue]]
}
