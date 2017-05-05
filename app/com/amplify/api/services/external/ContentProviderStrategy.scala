package com.amplify.api.services.external

import com.amplify.api.domain.models.ContentProviderType
import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.services.external.models.{PlaylistData, UserData}
import com.amplify.api.services.external.spotify.SpotifyContentProvider
import javax.inject.Inject
import scala.concurrent.Future

trait ContentProviderStrategy {

  def fetchUser(implicit token: String): Future[UserData]

  def fetchPlaylists(implicit token: String): Future[Seq[PlaylistData]]
}

class ContentProviderRegistry @Inject()(spotifyContentProvider: SpotifyContentProvider) {

  def getStrategy(
      contentProvider: ContentProviderType): ContentProviderStrategy = contentProvider match {
    case ContentProviderType.Spotify ⇒ spotifyContentProvider
  }
}
