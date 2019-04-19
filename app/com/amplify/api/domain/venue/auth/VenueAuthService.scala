package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Token, Uid}
import com.amplify.api.domain.models.{AuthToken, Venue}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueAuthServiceImpl])
trait VenueAuthService {

  def signIn(authorizationCode: AuthToken[AuthorizationCode]): Future[Venue]

  def login(venueUid: Uid): Future[Option[Venue]]

  def refreshToken(venue: Venue): Future[Venue]

  def withRefreshToken[T](venue: Venue)(f: Token[Access] ⇒ Future[T]): Future[T]
}
