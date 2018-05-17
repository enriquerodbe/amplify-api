package com.amplify.api.services

import com.amplify.api.domain.models.primitives.AuthorizationCode
import com.amplify.api.domain.models.{AuthToken, Venue, VenueData}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthServiceImpl])
trait VenueAuthService {

  def signIn(authorizationCode: AuthToken[AuthorizationCode]): Future[VenueData]

  def refreshToken(venue: Venue): Future[Venue]
}
