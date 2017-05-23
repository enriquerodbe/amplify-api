package com.amplify.api.services.external

import com.amplify.api.domain.models.{AuthToken, ContentProviderType, Track}
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.services.external.models.{PlaylistData, TrackData, UserData}
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import javax.inject.Inject
import scala.concurrent.Future

trait ContentProviderStrategy {

  def fetchUser(implicit token: AuthToken): Future[UserData]

  def fetchPlaylists(implicit token: AuthToken): Future[Seq[PlaylistData]]

  def fetchPlaylistTracks(
      userIdentifier: Identifier,
      playlistIdentifier: Identifier)(
      implicit token: AuthToken): Future[Seq[TrackData]]

  def play(tracks: Seq[Track])(implicit token: AuthToken): Future[Unit]

  def pause(implicit token: AuthToken): Future[Unit]
}

class ContentProviderRegistry @Inject()(spotifyContentProvider: SpotifyContentProvider) {

  def getStrategy(
      contentProvider: ContentProviderType): ContentProviderStrategy = contentProvider match {
    case ContentProviderType.Spotify ⇒ spotifyContentProvider
  }
}
