package com.amplify.api.services.external.spotify

import com.amplify.api.domain.models.AuthProviderType.{Spotify ⇒ AuthSpotify}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.models._
import com.amplify.api.services.external.spotify.Dtos._

object Converters {

  def userToUserData(user: User): UserData = UserData(AuthSpotify → user.id, Name(user.displayName))

  def imageToImageData(image: Image): ImageData = ImageData(image.url, image.height, image.width)

  def playlistToPlaylistData(playlist: Playlist): PlaylistData = {
    PlaylistData(Spotify → playlist.id, Name(playlist.name), playlist.images.map(imageToImageData))
  }

  def trackItemToTrackData(trackItem: TrackItem): TrackData = {
    val trackIdentifier = Spotify → trackItem.track.id
    TrackData(trackIdentifier, Name(trackItem.track.name), albumToAlbumData(trackItem.track.album))
  }

  def albumToAlbumData(album: Album): AlbumData = {
    val artists = album.artists.map(artistToArtistData)
    val albums = album.images.map(imageToImageData)
    AlbumData(Name(album.name), artists, albums)
  }

  def artistToArtistData(artist: Artist): ArtistData = ArtistData(Name(artist.name))
}
