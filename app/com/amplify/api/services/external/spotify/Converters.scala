package com.amplify.api.services.external.spotify

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType}
import com.amplify.api.services.external.models._
import com.amplify.api.services.external.spotify.Dtos._

object Converters {

  def userToUserData(user: User): UserData = {
    val identifier = ContentProviderIdentifier(ContentProviderType.Spotify, user.id)
    UserData(identifier, user.displayName)
  }

  def imageToImageData(image: Image): ImageData = ImageData(image.url, image.height, image.width)

  def playlistToPlaylistData(playlist: Playlist): PlaylistData = {
    val identifier = ContentProviderIdentifier(ContentProviderType.Spotify, playlist.id)
    PlaylistData(identifier, playlist.name, playlist.images.map(imageToImageData))
  }

  def trackItemToTrackData(trackItem: TrackItem): TrackData = {
    val trackIdentifier =
      ContentProviderIdentifier(ContentProviderType.Spotify, trackItem.track.uri)
    TrackData(trackIdentifier, trackItem.track.name, albumToAlbumData(trackItem.track.album))
  }

  def albumToAlbumData(album: Album): AlbumData = {
    AlbumData(album.name, album.artists.map(artistToArtistData), album.images.map(imageToImageData))
  }

  def artistToArtistData(artist: Artist): ArtistData = ArtistData(artist.name)
}
