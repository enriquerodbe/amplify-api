package com.amplify.api.services.external

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.domain.models.ContentProviderType.{ContentProviderType, Spotify}
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.services.models.{PlaylistData, TrackData}
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import javax.inject.Inject
import scala.concurrent.Future

trait ContentProviderStrategy {

  def fetchPlaylists(implicit token: AuthToken): Future[Seq[PlaylistData]]

  def fetchPlaylist(
      userIdentifier: Identifier,
      playlistIdentifier: Identifier)(
      implicit authToken: AuthToken): Future[PlaylistData]

  def fetchPlaylistTracks(
      userIdentifier: Identifier,
      playlistIdentifier: Identifier)(
      implicit token: AuthToken): Future[Seq[TrackData]]
}

class ContentProviderRegistry @Inject()(spotifyContentProvider: SpotifyContentProvider) {

  def getStrategy(contentProvider: ContentProviderType): ContentProviderStrategy = {
    contentProvider match {
      case Spotify ⇒ spotifyContentProvider
    }
  }
}
