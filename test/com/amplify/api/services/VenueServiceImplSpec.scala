package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.domain.models.primitives._
import com.amplify.api.domain.models.{AuthProviderIdentifier, AuthProviderType, AuthToken, Venue}
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.{ExternalAuthService, ExternalContentService}
import com.amplify.api.test.BaseUnitSpec
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.dbio.DBIO

class VenueServiceImplSpec extends BaseUnitSpec {

  trait TestContext {
    val db = {
      val mock = strictMock[DbioRunner]
      when(mock.run(any(classOf[DBIO[Unit]]))).thenReturn(Future.successful(()))
      mock
    }
    val authService = strictMock[ExternalAuthService]
    val contentService = strictMock[ExternalContentService]
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

    val venueService = new VenueServiceImpl(db, authService, contentService, venueDao)
  }

  "VenueServiceImpl" should {
    "refresh tokens" when {
      "access token not found" in new TestContext {
        when(contentService.startPlayback(Seq.empty, venue.accessToken))
            .thenReturn(Future.failed(UserAuthTokenNotFound))
          when(contentService.startPlayback(Seq.empty, accessToken))
            .thenReturn(Future.successful(()))

        await(venueService.startPlayback(venue, Seq.empty))

        verify(contentService).startPlayback(Seq.empty, venue.accessToken)
        verify(contentService).startPlayback(Seq.empty, accessToken)
      }
    }
  }
}
