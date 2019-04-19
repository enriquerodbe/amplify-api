package com.amplify.api.shared.services

import com.amplify.api.domain.models.primitives._
import com.amplify.api.domain.models.{AuthProviderIdentifier, AuthProviderType, AuthToken, Venue}
import com.amplify.api.domain.venue.{VenueDao, VenueService}
import com.amplify.api.domain.venue.auth.{VenueAuthServiceImpl, VenueExternalAuthService}
import com.amplify.api.shared.daos.DbioRunner
import com.amplify.api.shared.exceptions.UserAuthTokenNotFound
import com.amplify.api.test.BaseUnitSpec
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.dbio.DBIO

class VenueAuthServiceImplSpec extends BaseUnitSpec {

  trait TestContext {
    val db = {
      val mock = strictMock[DbioRunner]
      when(mock.run(any(classOf[DBIO[Unit]]))).thenReturn(Future.successful(()))
      mock
    }
    val venueService = strictMock[VenueService]
    val authService = strictMock[VenueExternalAuthService]
    val venueDao = strictMock[VenueDao]

    val venue =
      Venue(
        name = Name("Test"),
        uid = Uid(),
        identifier = AuthProviderIdentifier(AuthProviderType.Spotify, Identifier("test-ident")),
        refreshToken = Token("test-refresh-token"),
        accessToken = Token("test-access-token"))

    val authToken = AuthToken(venue.identifier.authProvider, venue.refreshToken)
    val accessToken = Token[Access]("refreshed-refresh-token")

    when(authService.refreshAccessToken(authToken)).thenReturn(Future.successful(accessToken))
    when(venueDao.updateAccessToken(venue, accessToken)).thenReturn(DBIO.successful(()))

    val venueAuthService = new VenueAuthServiceImpl(db, venueService, venueDao, authService)
  }

  "VenueAuthServiceImpl" should {
    "refresh tokens" when {
      "access token not found" in new TestContext {
        private def validation(token: Token[Access]) = {
          if (token == accessToken) Future.successful(())
          else Future.failed(UserAuthTokenNotFound)
        }

        await(venueAuthService.withRefreshToken(venue)(validation))

        verify(authService).refreshAccessToken(authToken)
        verify(venueDao).updateAccessToken(venue, accessToken)
      }
    }
  }
}
