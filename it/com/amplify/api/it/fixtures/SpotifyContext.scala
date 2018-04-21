package com.amplify.api.it.fixtures

import com.amplify.api.controllers.auth.AuthHeadersUtil
import com.amplify.api.domain.models.AuthProviderType.{Spotify ⇒ AuthSpotify}
import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.Spotify.PlaylistUri
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.spotify.Dtos._
import com.amplify.api.services.external.spotify.{SpotifyAuthProvider, SpotifyContentProvider}
import com.amplify.api.services.models._
import org.mockito.Mockito.{RETURNS_SMART_NULLS, when, withSettings}
import org.scalatest.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.mvc.Http.HeaderNames
import scala.concurrent.Future

trait SpotifyContext extends CommonData with MockitoSugar {

  val spotifyContentProvider =
    mock[SpotifyContentProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  val spotifyAuthProvider =
    mock[SpotifyAuthProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))

  implicit val aliceAuthToken = AuthToken(AuthSpotify, aliceToken)
  val bobAuthToken = AuthToken(AuthSpotify, bobToken)
  val invalidAuthToken = AuthToken(AuthSpotify, invalidToken)

  implicit class FakeRequestWithAuthToken[T](fakeRequest: FakeRequest[T]) {

    def tokenHeader(token: String): (String, String) = HeaderNames.AUTHORIZATION → s"Bearer $token"

    def withAuthToken(token: String): FakeRequest[T] = {
      fakeRequest.withHeaders(tokenHeader(token))
    }

    def withAliceToken: FakeRequest[T] = withAuthToken(aliceToken)

    def withBobToken: FakeRequest[T] = withAuthToken(bobToken)
  }

  implicit class FakeRequestWithCoin[T](fakeRequest: FakeRequest[T]) {

    def coinHeader(coin: String): (String, String) = AuthHeadersUtil.COIN_PARAM → coin

    def withCoin(coin: String): FakeRequest[T] = fakeRequest.withHeaders(coinHeader(coin))

    def withValidCoin: FakeRequest[T] = withCoin(s"$aliceVenueUid:$validCoinTokenStr")
  }

  val aliceUserData = UserData(AuthSpotify → aliceSpotifyId, "Alice")
  val aliceSpotifyUser = User(aliceSpotifyId, "Alice")
  val trashAlbum = Album("Trash", Seq(Artist("Alice Cooper")), Seq.empty)
  val poisonTrack = TrackItem(Track("poison-id", "Poison", trashAlbum))
  val bedOfNailsTrack = TrackItem(Track("bed_of_nails-id", "Bed of Nails", trashAlbum))
  val alicePlaylistTracks = Seq(poisonTrack, bedOfNailsTrack)
  val alicePlaylistImages = Seq(Image("url", Some(360), Some(360)))
  val alicePlaylistUri = PlaylistUri(aliceSpotifyId, "alice-playlist-id")
  val alicePlaylist =
    Playlist(alicePlaylistUri.id, aliceSpotifyUser, "Alice playlist", alicePlaylistImages)
  val bobUserData = UserData(AuthSpotify → bobSpotifyId, "Bob Marley")

  when(spotifyAuthProvider.fetchUser(aliceAuthToken)).thenReturn(Future.successful(aliceUserData))
  when(spotifyAuthProvider.fetchUser(bobAuthToken)).thenReturn(Future.successful(bobUserData))
  when(spotifyAuthProvider.fetchUser(invalidAuthToken))
    .thenReturn(Future.failed(UserAuthTokenNotFound))
  when(spotifyContentProvider.fetchPlaylists(aliceAuthToken))
    .thenReturn(Future.successful(Seq(alicePlaylist)))
  when(spotifyContentProvider.fetchPlaylist(alicePlaylistUri))
    .thenReturn(Future.successful(alicePlaylist))
  when(spotifyContentProvider.fetchPlaylistTracks(alicePlaylistUri))
    .thenReturn(Future.successful(alicePlaylistTracks))
}
