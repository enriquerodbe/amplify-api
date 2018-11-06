package com.amplify.api.services

import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Token}
import com.amplify.api.domain.models.{AuthToken, Venue, VenueData}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthServiceImpl])
trait VenueAuthService {

  def signIn(authorizationCode: AuthToken[AuthorizationCode]): Future[VenueData]

  def refreshToken(venue: Venue): Future[Venue]

  def withRefreshToken[T](venue: Venue)(f: Token[Access] ⇒ Future[T]): Future[T]
}
