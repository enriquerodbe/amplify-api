package com.amplify.api.it.fixtures

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.models.{PlaylistData, UserData}
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.mvc.Http.HeaderNames
import scala.concurrent.Future

trait SpotifyContext {

  def spotifyProvider: SpotifyContentProvider

  val authorizationHeader = HeaderNames.AUTHORIZATION
  val aliceToken = "alice-token"
  val bobToken = "bob-token"
  val invalidToken = "invalid-token"

  val aliceAuthToken = AuthToken(Spotify, aliceToken)
  val bobAuthToken = AuthToken(Spotify, bobToken)
  val invalidAuthToken = AuthToken(Spotify, invalidToken)

  def tokenHeader(token: String): (String, String) = authorizationHeader → s"Bearer $token"

  implicit class FakeRequestWithAuthToken[T](fakeRequest: FakeRequest[T]) {

    def withAuthToken(token: String): FakeRequest[T] = {
      fakeRequest.withHeaders(tokenHeader(token))
    }

    def withAliceToken: FakeRequest[T] = withAuthToken(aliceToken)

    def withBobToken: FakeRequest[T] = withAuthToken(bobToken)
  }

  def venueRequest(name: String): FakeRequest[VenueRequest] = {
    FakeRequest().withBody(VenueRequest(name))
  }

  val aliceUserData = UserData(Spotify → "alice-spotify-id", "Alice Cooper")
  val alicePlaylistData = PlaylistData(Spotify → "alice-playlist", "Alice playlist", Seq.empty)
  val bobUserData = UserData(Spotify → "bob-spotify-id", "Bob Marley")

  when(spotifyProvider.fetchUser(aliceAuthToken)).thenReturn(Future.successful(aliceUserData))
  when(spotifyProvider.fetchUser(bobAuthToken)).thenReturn(Future.successful(bobUserData))
  when(spotifyProvider.fetchUser(invalidAuthToken)).thenReturn(Future.failed(UserAuthTokenNotFound))
  when(spotifyProvider.fetchPlaylists(aliceAuthToken))
    .thenReturn(Future.successful(Seq(alicePlaylistData)))
}
