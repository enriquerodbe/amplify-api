package com.amplify.api.domain.venue.auth

import com.amplify.api.domain.models.primitives.{Access, AuthorizationCode, Refresh, Token}
import com.amplify.api.domain.models.{AuthToken, Venue, VenueData}
import com.amplify.api.domain.venue.VenueDao
import com.amplify.api.shared.daos.DbioRunner
import com.amplify.api.shared.exceptions.UserAuthTokenNotFound
import com.amplify.api.shared.services.external.models.RefreshTokens
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueAuthServiceImpl @Inject()(
    db: DbioRunner,
    venueDao: VenueDao,
    authService: VenueExternalAuthService)(
    implicit ec: ExecutionContext) extends VenueAuthService {

  override def signIn(authCode: AuthToken[AuthorizationCode]): Future[VenueData] = {
    for {
      RefreshTokens(refreshToken, accessToken) ← authService.requestRefreshAndAccessTokens(authCode)
      userData ← authService.fetchUser(AuthToken(authCode.authProvider, accessToken))
    }
    yield VenueData(userData.identifier, userData.name, refreshToken, accessToken)
  }

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
