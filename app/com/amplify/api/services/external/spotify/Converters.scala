package com.amplify.api.services.external.spotify

import com.amplify.api.domain.models.{ContentProviderIdentifier, ContentProviderType}
import com.amplify.api.services.external.models.{PlaylistData, TrackData, UserData}
import com.amplify.api.services.external.spotify.Dtos.{Playlist, Track, TrackItem, User}

object Converters {

  def userToUserData(user: User): UserData = {
    val identifier = ContentProviderIdentifier(ContentProviderType.Spotify, user.id)
    UserData(identifier, user.displayName, user.email)
  }

  def playlistToPlaylistData(playlist: Playlist): PlaylistData = {
    val identifier = ContentProviderIdentifier(ContentProviderType.Spotify, playlist.id)
    PlaylistData(identifier, playlist.name)
  }

  def trackItemToTrackData(trackItem: TrackItem): TrackData = {
    val trackIdentifier = ContentProviderIdentifier(ContentProviderType.Spotify, trackItem.track.id)
    TrackData(trackIdentifier, trackItem.track.name)
  }
}
