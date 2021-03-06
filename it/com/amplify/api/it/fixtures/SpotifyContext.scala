package com.amplify.api.it.fixtures

import com.amplify.api.domain.coin.CoinAuthenticatedBuilder.COIN_PARAM
import com.amplify.api.domain.models.AuthProviderType.{Spotify ⇒ AuthSpotify}
import com.amplify.api.domain.models.Spotify.{PlaylistUri, TrackUri}
import com.amplify.api.shared.exceptions.UserAuthTokenNotFound
import com.amplify.api.shared.services.external.models._
import com.amplify.api.shared.services.external.spotify.Dtos._
import com.amplify.api.shared.services.external.spotify.{SpotifyAuthProvider, SpotifyContentProvider}
import org.mockito.Mockito.{RETURNS_SMART_NULLS, when, withSettings}
import org.scalatest.mockito.MockitoSugar
import play.api.test.FakeRequest
import scala.concurrent.Future

trait SpotifyContext extends CommonData with MockitoSugar {

  val spotifyContentProvider =
    mock[SpotifyContentProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))
  val spotifyAuthProvider =
    mock[SpotifyAuthProvider](withSettings().defaultAnswer(RETURNS_SMART_NULLS))

  implicit class FakeRequestWithCookie[T](fakeRequest: FakeRequest[T]) {

    def cookie(venueUid: String): (String, String) = "venue-uid" → venueUid

    def withSession(venueUid: String): FakeRequest[T] = fakeRequest.withSession(cookie(venueUid))

    def withAliceSession: FakeRequest[T] = withSession(aliceVenueUid)
  }

  implicit class FakeRequestWithCoin[T](fakeRequest: FakeRequest[T]) {

    def coinHeader(coin: String): (String, String) = COIN_PARAM → coin

    def withCoin(coin: String): FakeRequest[T] = fakeRequest.withHeaders(coinHeader(coin))

    def withUnusedCoin: FakeRequest[T] = withCoin(unusedCoinCode)
    def withUsedCoin: FakeRequest[T] = withCoin(usedCoinCode)
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

  def mockSpotify(): Unit = {
    when(spotifyAuthProvider.requestRefreshAndAccessTokens(aliceCode))
      .thenReturn(Future.successful(aliceRefreshToken → aliceAccessToken))
    when(spotifyAuthProvider.requestRefreshAndAccessTokens(bobCode))
      .thenReturn(Future.successful(bobRefreshToken → bobAccessToken))
    when(spotifyAuthProvider.requestRefreshAndAccessTokens(invalidAuthCode))
      .thenReturn(Future.failed(UserAuthTokenNotFound))
    when(spotifyAuthProvider.refreshAccessToken(aliceRefreshToken))
      .thenReturn(Future.successful(aliceAccessToken))
    when(spotifyAuthProvider.fetchUser(aliceAccessToken))
      .thenReturn(Future.successful(aliceUserData))
    when(spotifyAuthProvider.fetchUser(bobAccessToken)).thenReturn(Future.successful(bobUserData))
    when(spotifyAuthProvider.fetchUser(invalidAccessToken))
      .thenReturn(Future.failed(UserAuthTokenNotFound))
    when(spotifyContentProvider.fetchPlaylists(aliceAccessToken))
      .thenReturn(Future.successful(Seq(alicePlaylist)))
    when(spotifyContentProvider.fetchPlaylist(alicePlaylistUri, aliceAccessToken))
      .thenReturn(Future.successful(alicePlaylist))
    when(spotifyContentProvider.fetchPlaylistTracks(alicePlaylistUri, aliceAccessToken))
      .thenReturn(Future.successful(alicePlaylistTracks))
    when(spotifyContentProvider
      .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), invalidAccessToken))
      .thenReturn(Future.failed(UserAuthTokenNotFound))
    when(spotifyContentProvider
      .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), aliceAccessToken))
      .thenReturn(Future.successful(()))
    when(spotifyContentProvider
      .fetchTrack(TrackUri(bedOfNailsTrack.track.id), aliceAccessToken))
      .thenReturn(Future.successful(bedOfNailsTrack))
  }
}
