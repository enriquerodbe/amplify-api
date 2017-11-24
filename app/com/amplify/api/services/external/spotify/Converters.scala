package com.amplify.api.services.external.spotify

import com.amplify.api.domain.models.AuthProviderType.{Spotify ⇒ AuthSpotify}
import com.amplify.api.domain.models.ContentProviderType.Spotify
import com.amplify.api.services.models._
import com.amplify.api.services.external.spotify.Dtos._

object Converters {

  def userToUserData(user: User): UserData = UserData(AuthSpotify → user.id, user.displayName)

  def imageToImageData(image: Image): ImageData = ImageData(image.url, image.height, image.width)

  def playlistToPlaylistData(playlist: Playlist): PlaylistData = {
    PlaylistData(Spotify → playlist.id, playlist.name, playlist.images.map(imageToImageData))
  }

  def trackItemToTrackData(trackItem: TrackItem): TrackData = {
    val trackIdentifier = Spotify → trackItem.track.id
    TrackData(trackIdentifier, trackItem.track.name, albumToAlbumData(trackItem.track.album))
  }

  def albumToAlbumData(album: Album): AlbumData = {
    AlbumData(album.name, album.artists.map(artistToArtistData), album.images.map(imageToImageData))
  }

  def artistToArtistData(artist: Artist): ArtistData = ArtistData(artist.name)
}
