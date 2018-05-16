package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.{AuthorizationCode, Uid}
import com.amplify.api.domain.models.{AuthToken, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthLogicImpl])
trait VenueAuthLogic {

  def signIn(authorizationCode: AuthToken[AuthorizationCode]): Future[Venue]

  def login(venueUid: Uid): Future[Option[Venue]]
}
