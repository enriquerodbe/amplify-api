package com.amplify.api.services.external.spotify

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType}
import com.amplify.api.services.external.models.{ImageData, PlaylistData, TrackData, UserData}
import com.amplify.api.services.external.spotify.Dtos.{Image, Playlist, TrackItem, User}

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
    val trackIdentifier = ContentProviderIdentifier(ContentProviderType.Spotify, trackItem.track.id)
    TrackData(trackIdentifier, trackItem.track.name)
  }
}
