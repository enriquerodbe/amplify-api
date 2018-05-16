package com.amplify.api.domain.logic

import com.amplify.api.domain.models.primitives.{AuthorizationCode, Uid}
import com.amplify.api.domain.models.{AuthToken, Venue}
import com.amplify.api.services.{VenueAuthService, VenueService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthLogicImpl @Inject()(
    venueAuthService: VenueAuthService,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends VenueAuthLogic {

  override def signIn(authorizationCode: AuthToken[AuthorizationCode]): Future[Venue] = {
    venueAuthService.signIn(authorizationCode).flatMap(venueService.retrieveOrCreate)
  }

  override def login(venueUid: Uid): Future[Option[Venue]] = venueService.retrieve(venueUid)
}
