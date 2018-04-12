package com.amplify.api.services.external.spotify

import com.amplify.api.domain.models
import com.amplify.api.domain.models.AuthProviderType.{Spotify ⇒ AuthSpotify}
import com.amplify.api.domain.models.Spotify.{PlaylistUri, TrackUri}
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.services.external.spotify.Dtos._
import com.amplify.api.services.models._

object Converters {

  def userToUserData(user: User): UserData = UserData(AuthSpotify → user.id, Name(user.displayName))

  def toModelImage(image: Image): models.Image = models.Image(image.url, image.height, image.width)

  def toModelPlaylist(playlist: Playlist): models.PlaylistInfo = {
    models.PlaylistInfo(
      Name(playlist.name),
      PlaylistUri(playlist.owner.id, playlist.id),
      playlist.images.map(toModelImage))
  }

  def toModelTrack(trackItem: TrackItem): models.Track = {
    models.Track(
      Name(trackItem.track.name),
      TrackUri(trackItem.track.id),
      toModelAlbum(trackItem.track.album))
  }

  def toModelAlbum(album: Album): models.Album = {
    val artists = album.artists.map(toModelArtist)
    val images = album.images.map(toModelImage)
    models.Album(Name(album.name), artists, images)
  }

  def toModelArtist(artist: Artist): models.Artist = models.Artist(Name(artist.name))
}
