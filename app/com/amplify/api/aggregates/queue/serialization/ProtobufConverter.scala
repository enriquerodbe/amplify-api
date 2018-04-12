package com.amplify.api.aggregates.queue.serialization

import com.amplify.api.aggregates.queue.Event
import com.amplify.api.aggregates.queue.serialization.ContentProvider.CONTENT_PROVIDER_SPOTIFY
import com.amplify.api.domain.models
import com.amplify.api.domain.models.Spotify.{PlaylistUri, TrackUri}
import com.amplify.api.exceptions.InvalidProviderIdentifier
import scalapb.GeneratedMessage

object ProtobufConverter {

  private val DEFAULT_INT = 0

  def toProtobuf(evt: Event): GeneratedMessage = evt match {
    case Event.CurrentPlaylistSet(playlist) ⇒ CurrentPlaylistSet(Some(toProtobuf(playlist)))
    case Event.VenueTracksRemoved ⇒ VenueTracksRemoved()
    case Event.VenueTrackAdded(track) ⇒ VenueTrackAdded(Some(toProtobuf(track)))
    case Event.TrackFinished ⇒ TrackFinished()
    case Event.UserTrackAdded(user, trackIdentifier) ⇒
      UserTrackAdded(Some(toProtobuf(user)), Some(toProtobuf(trackIdentifier)))
    case Event.CurrentTrackSkipped ⇒ CurrentTrackSkipped()
  }

  private def toProtobuf(playlist: models.Playlist): Playlist = {
    val name = Some(Name(playlist.info.name.value))
    val identifier = Some(toProtobuf(playlist.info.identifier))
    val images = playlist.info.images.map(toProtobuf)
    val playlistInfo = Some(PlaylistInfo(name, identifier, images))
    val tracks = playlist.tracks.map(toProtobuf)
    Playlist(playlistInfo, tracks)
  }

  private def toProtobuf(contentIdentifier: models.PlaylistIdentifier): PlaylistIdentifier = {
    val provider = ContentProvider.fromValue(contentIdentifier.contentProvider.id)
    PlaylistIdentifier(provider, Some(Identifier(contentIdentifier.identifier.value)))
  }

  private def toProtobuf(contentIdentifier: models.TrackIdentifier): TrackIdentifier = {
    val provider = ContentProvider.fromValue(contentIdentifier.contentProvider.id)
    TrackIdentifier(provider, Some(Identifier(contentIdentifier.identifier.value)))
  }

  private def toProtobuf(
      authIdentifier: models.AuthProviderIdentifier): AuthProviderIdentifier = {
    val provider = AuthProviderType.fromValue(authIdentifier.authProvider.id)
    val identifier = Some(Identifier(authIdentifier.identifier.value))
    AuthProviderIdentifier(provider, identifier)
  }

  private def toProtobuf(image: models.Image): Image = {
    Image(image.url, image.height.getOrElse(DEFAULT_INT), image.width.getOrElse(DEFAULT_INT))
  }

  private def toProtobuf(track: models.Track): Track = {
    val name = Some(Name(track.name.value))
    val identifier = Some(toProtobuf(track.identifier))
    val album = Some(toProtobuf(track.album))
    Track(name, identifier, album)
  }

  private def toProtobuf(album: models.Album): Album = {
    Album(Some(Name(album.name.value)), album.artists.map(toProtobuf), album.images.map(toProtobuf))
  }

  private def toProtobuf(artist: models.Artist): Artist = {
    Artist(Some(Name(artist.name.value)))
  }

  private def toProtobuf(user: models.User): User = {
    User(Some(Name(user.name.value)), Some(toProtobuf(user.identifier)))
  }

  def fromProtobuf(message: GeneratedMessage): Event = message match {
    case c: CurrentPlaylistSet ⇒ Event.CurrentPlaylistSet(fromProtobuf(c.getPlaylist))
    case _: VenueTracksRemoved ⇒ Event.VenueTracksRemoved
    case a: VenueTrackAdded ⇒ Event.VenueTrackAdded(fromProtobuf(a.getTrack))
    case _: TrackFinished ⇒ Event.TrackFinished
    case a: UserTrackAdded ⇒
      Event.UserTrackAdded(fromProtobuf(a.getUser), fromProtobuf(a.getTrackIdentifier))
    case _: CurrentTrackSkipped ⇒ Event.CurrentTrackSkipped
  }

  private def fromProtobuf(playlist: Playlist): models.Playlist = {
    models.Playlist(fromProtobuf(playlist.getPlaylistInfo), playlist.tracks.map(fromProtobuf))
  }

  private def fromProtobuf(info: PlaylistInfo): models.PlaylistInfo = {
    val name = models.primitives.Name(info.getName.value)
    val identifier = fromProtobuf(info.getIdentifier)
    models.PlaylistInfo(name, identifier, info.images.map(fromProtobuf))
  }

  private def fromProtobuf(contentIdentifier: PlaylistIdentifier): models.PlaylistIdentifier = {
    contentIdentifier.contentProvider match {
      case CONTENT_PROVIDER_SPOTIFY ⇒
        PlaylistUri.fromString(contentIdentifier.getIdentifier.value).get
      case _ ⇒
        throw InvalidProviderIdentifier(contentIdentifier.getIdentifier.value)
    }
  }

  private def fromProtobuf(contentIdentifier: TrackIdentifier): models.TrackIdentifier = {
    contentIdentifier.contentProvider match {
      case CONTENT_PROVIDER_SPOTIFY ⇒
        TrackUri(contentIdentifier.getIdentifier.value)
      case _ ⇒
        throw InvalidProviderIdentifier(contentIdentifier.getIdentifier.value)
    }
  }

  private def fromProtobuf(
      identifier: AuthProviderIdentifier): models.AuthProviderIdentifier = {
    val provider = models.AuthProviderType(identifier.authProvider.value)
    val id = models.primitives.Identifier(identifier.getIdentifier.value)
    models.AuthProviderIdentifier(provider, id)
  }

  private def fromProtobuf(image: Image): models.Image = {
    val height = if (image.height == DEFAULT_INT) None else Some(image.height)
    val width = if (image.width == DEFAULT_INT) None else Some(image.width)
    models.Image(image.url, height, width)
  }

  private def fromProtobuf(track: Track): models.Track = {
    val name = models.primitives.Name(track.getName.value)
    models.Track(name, fromProtobuf(track.getIdentifier), fromProtobuf(track.getAlbum))
  }

  private def fromProtobuf(album: Album): models.Album = {
    val name = models.primitives.Name(album.getName.value)
    val artists = album.artists.map(fromProtobuf)
    val images = album.images.map(fromProtobuf)
    models.Album(name, artists, images)
  }

  private def fromProtobuf(artist: Artist): models.Artist = {
    models.Artist(models.primitives.Name(artist.getName.value))
  }

  private def fromProtobuf(user: User): models.User = {
    models.User(models.primitives.Name(user.getName.value), fromProtobuf(user.getIdentifier))
  }
}
