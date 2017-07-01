package com.amplify.api.it.fixtures

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.models.UserData
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.mvc.Http.HeaderNames
import scala.concurrent.Future

trait SpotifyContext {

  def spotifyProvider: SpotifyContentProvider

  val authorizationHeader = HeaderNames.AUTHORIZATION
  val validToken = "valid-token"
  val invalidToken = "invalid-token"

  val validAuthToken = AuthToken(Spotify, validToken)
  val invalidAuthToken = AuthToken(Spotify, invalidToken)

  def venueRequest(name: String): FakeRequest[VenueRequest] = {
    FakeRequest().withBody(VenueRequest(name))
  }
  def venueRequest(name: String, token: String): FakeRequest[VenueRequest] = {
    venueRequest(name).withHeaders(authorizationHeader → s"Bearer $token")
  }
  def validTokenVenueRequest(name: String): FakeRequest[VenueRequest] = {
    venueRequest(name, validToken)
  }
  def invalidTokenVenueRequest(name: String): FakeRequest[VenueRequest] = {
    venueRequest(name, invalidToken)
  }

  val userData = UserData(Spotify → "id", "UserName")

  when(spotifyProvider.fetchUser(validAuthToken)).thenReturn(Future.successful(userData))
  when(spotifyProvider.fetchUser(invalidAuthToken)).thenReturn(Future.failed(UserAuthTokenNotFound))
}
