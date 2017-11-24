package com.amplify.api.it.fixtures

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.AuthProviderType.{Spotify ⇒ AuthSpotify}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.models._
import com.amplify.api.services.external.spotify.{SpotifyAuthProvider, SpotifyContentProvider}
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.mvc.Http.HeaderNames
import scala.concurrent.Future

trait SpotifyContext extends CommonData {

  def spotifyContentProvider: SpotifyContentProvider
  def spotifyAuthProvider: SpotifyAuthProvider

  val authorizationHeader = HeaderNames.AUTHORIZATION
  val aliceToken = "alice-token"
  val bobToken = "bob-token"
  val invalidToken = "invalid-token"

  implicit val aliceAuthToken = AuthToken(AuthSpotify, aliceToken)
  val bobAuthToken = AuthToken(AuthSpotify, bobToken)
  val invalidAuthToken = AuthToken(AuthSpotify, invalidToken)

  def tokenHeader(token: String): (String, String) = authorizationHeader → s"Bearer $token"

  implicit class FakeRequestWithAuthToken[T](fakeRequest: FakeRequest[T]) {

    def withAuthToken(token: String): FakeRequest[T] = {
      fakeRequest.withHeaders(tokenHeader(token))
    }

    def withAliceToken: FakeRequest[T] = withAuthToken(aliceToken)

    def withBobToken: FakeRequest[T] = withAuthToken(bobToken)
  }

  val aliceUserData = UserData(AuthSpotify → aliceSpotifyId, "Alice Cooper")
  val alicePlaylistIdentifier = "alice-playlist"
  val trashAlbumData = AlbumData("Trash", Seq(ArtistData("Alice Cooper")), Seq.empty)
  val poisonTrackData = TrackData(Spotify → "track:poison", "Poison", trashAlbumData)
  val bedOfNailsTrackData =
    TrackData(Spotify → "track:bed_of_nails", "Bed of Nails", trashAlbumData)
  val alicePlaylistTracks = Seq(poisonTrackData, bedOfNailsTrackData)
  val alicePlaylistImages = Seq(ImageData("url", Some(360), Some(360)))
  val alicePlaylistData =
    PlaylistData(Spotify → alicePlaylistIdentifier, "Alice playlist", alicePlaylistImages)
  val bobUserData = UserData(AuthSpotify → bobSpotifyId, "Bob Marley")

  when(spotifyAuthProvider.fetchUser(aliceAuthToken)).thenReturn(Future.successful(aliceUserData))
  when(spotifyAuthProvider.fetchUser(bobAuthToken)).thenReturn(Future.successful(bobUserData))
  when(spotifyAuthProvider.fetchUser(invalidAuthToken))
    .thenReturn(Future.failed(UserAuthTokenNotFound))
  when(spotifyContentProvider.fetchPlaylists(aliceAuthToken))
    .thenReturn(Future.successful(Seq(alicePlaylistData)))
  when(spotifyContentProvider.fetchPlaylist(aliceSpotifyId, alicePlaylistIdentifier))
    .thenReturn(Future.successful(alicePlaylistData))
  when(spotifyContentProvider.fetchPlaylistTracks(aliceSpotifyId, alicePlaylistIdentifier))
    .thenReturn(Future.successful(alicePlaylistTracks))
}
