package com.amplify.api.it.fixtures

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.models._
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.mvc.Http.HeaderNames
import scala.concurrent.Future

trait SpotifyContext extends CommonData {

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

  val aliceUserData = UserData(Spotify → aliceSpotifyId, "Alice Cooper")
  val alicePlaylistIdentifier = "alice-playlist"
  val trashAlbumData = AlbumData("Trash", Seq(ArtistData("Alice Cooper")), Seq.empty)
  val poisonTrackData = TrackData(Spotify → "track:poison", "Poison", trashAlbumData)
  val alicePlaylistTracks = Seq(poisonTrackData)
  val alicePlaylistImages = Seq(ImageData("url", Some(360), Some(360)))
  val alicePlaylistData =
    PlaylistData(Spotify → alicePlaylistIdentifier, "Alice playlist", alicePlaylistImages)
  val bobUserData = UserData(Spotify → bobSpotifyId, "Bob Marley")

  when(spotifyProvider.fetchUser(aliceAuthToken)).thenReturn(Future.successful(aliceUserData))
  when(spotifyProvider.fetchUser(bobAuthToken)).thenReturn(Future.successful(bobUserData))
  when(spotifyProvider.fetchUser(invalidAuthToken)).thenReturn(Future.failed(UserAuthTokenNotFound))
  when(spotifyProvider.fetchPlaylists(aliceAuthToken))
    .thenReturn(Future.successful(Seq(alicePlaylistData)))
  when(spotifyProvider.fetchPlaylist(aliceSpotifyId, alicePlaylistIdentifier)(aliceAuthToken))
    .thenReturn(Future.successful(alicePlaylistData))
  when(spotifyProvider.fetchPlaylistTracks(aliceSpotifyId, alicePlaylistIdentifier)(aliceAuthToken))
    .thenReturn(Future.successful(alicePlaylistTracks))
}
