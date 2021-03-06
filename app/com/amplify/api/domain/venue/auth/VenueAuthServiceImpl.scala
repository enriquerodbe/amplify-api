package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.primitives._
import com.amplify.api.domain.models.{AuthToken, Venue, VenueData}
import com.amplify.api.domain.venue.{VenueDao, VenueService}
import com.amplify.api.shared.daos.DbioRunner
import com.amplify.api.shared.exceptions.UserAuthTokenNotFound
import com.amplify.api.shared.services.external.models.RefreshTokens
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

private class VenueAuthServiceImpl @Inject()(
    db: DbioRunner,
    venueService: VenueService,
    venueDao: VenueDao,
    authService: VenueExternalAuthService)(
    implicit ec: ExecutionContext) extends VenueAuthService {

  override def signIn(authorizationCode: AuthToken[AuthorizationCode]): Future[Venue] = {
    doSignIn(authorizationCode).flatMap(venueService.retrieveOrCreate)
  }

  private def doSignIn(authCode: AuthToken[AuthorizationCode]): Future[VenueData] = {
    for {
      RefreshTokens(refreshToken, accessToken) ← authService.requestRefreshAndAccessTokens(authCode)
      userData ← authService.fetchUser(AuthToken(authCode.authProvider, accessToken))
    }
    yield VenueData(userData.identifier, userData.name, refreshToken, accessToken)
  }

  override def login(venueUid: Uid): Future[Option[Venue]] = venueService.retrieve(venueUid)

  override def refreshToken(venue: Venue): Future[Venue] = {
    val refreshToken = AuthToken[Refresh](venue.identifier.authProvider, venue.refreshToken)
    for {
      accessToken ← authService.refreshAccessToken(refreshToken)
      _ ← db.run(venueDao.updateAccessToken(venue, accessToken))
    }
    yield venue.copy(accessToken = accessToken)
  }

  override def withRefreshToken[T](venue: Venue)(f: Token[Access] ⇒ Future[T]): Future[T] = {
    f(venue.accessToken).recoverWith {
      case UserAuthTokenNotFound ⇒
        refreshToken(venue).flatMap(refreshedVenue ⇒ f(refreshedVenue.accessToken))
    }
  }
}
