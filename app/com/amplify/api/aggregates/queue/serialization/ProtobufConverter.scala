package com.amplify.api.aggregates.queue.serialization

import com.amplify.api.aggregates.queue.Event
import com.amplify.api.aggregates.queue.serialization.PbContentProvider.CONTENT_PROVIDER_SPOTIFY
import com.amplify.api.domain.models
import com.amplify.api.domain.models.Spotify.{PlaylistUri, TrackUri}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.exceptions.InvalidProviderIdentifier
import scalapb.GeneratedMessage

object ProtobufConverter {

  private val DEFAULT_INT = 0

  def toProtobuf(evt: Event): GeneratedMessage = evt match {
    case Event.CurrentPlaylistSet(playlist) ⇒ PbCurrentPlaylistSet(Some(toProtobuf(playlist)))
    case Event.PlaybackStarted ⇒ PbPlaybackStarted()
    case Event.VenueTracksRemoved ⇒ PbVenueTracksRemoved()
    case Event.VenueTrackAdded(track) ⇒ PbVenueTrackAdded(Some(toProtobuf(track)))
    case Event.TrackFinished ⇒ PbTrackFinished()
    case Event.UserTrackAdded(coin, trackIdentifier) ⇒
      PbUserTrackAdded(Some(toProtobuf(coin)), Some(toProtobuf(trackIdentifier)))
    case Event.CurrentTrackSkipped ⇒ PbCurrentTrackSkipped()
  }

  private def toProtobuf(playlist: Playlist): PbPlaylist = {
    val name = Some(PbName(playlist.info.name.value))
    val identifier = Some(toProtobuf(playlist.info.identifier))
    val images = playlist.info.images.map(toProtobuf)
    val playlistInfo = Some(PbPlaylistInfo(name, identifier, images))
    val tracks = playlist.tracks.map(toProtobuf)
    PbPlaylist(playlistInfo, tracks)
  }

  private def toProtobuf(contentIdentifier: PlaylistIdentifier): PbPlaylistIdentifier = {
    val provider = PbContentProvider.fromValue(contentIdentifier.contentProvider.id)
    PbPlaylistIdentifier(provider, Some(PbIdentifier(contentIdentifier.identifier.value)))
  }

  private def toProtobuf(contentIdentifier: TrackIdentifier): PbTrackIdentifier = {
    val provider = PbContentProvider.fromValue(contentIdentifier.contentProvider.id)
    PbTrackIdentifier(provider, Some(PbIdentifier(contentIdentifier.identifier.value)))
  }

  private def toProtobuf(image: Image): PbImage = {
    PbImage(image.url, image.height.getOrElse(DEFAULT_INT), image.width.getOrElse(DEFAULT_INT))
  }

  private def toProtobuf(track: Track): PbTrack = {
    val name = Some(PbName(track.name.value))
    val identifier = Some(toProtobuf(track.identifier))
    val album = Some(toProtobuf(track.album))
    PbTrack(name, identifier, album)
  }

  private def toProtobuf(album: Album): PbAlbum = {
    val artists = album.artists.map(toProtobuf)
    val albums = album.images.map(toProtobuf)
    PbAlbum(Some(PbName(album.name.value)), artists, albums)
  }

  private def toProtobuf(artist: models.Artist): PbArtist = {
    PbArtist(Some(PbName(artist.name.value)))
  }

  private def toProtobuf(coinToken: CoinToken): PbCoinToken = {
    PbCoinToken(Some(PbUid(coinToken.venueUid.value)), coinToken.token)
  }

  def fromProtobuf(message: GeneratedMessage): Event = message match {
    case c: PbCurrentPlaylistSet ⇒ Event.CurrentPlaylistSet(fromProtobuf(c.getPlaylist))
    case _: PbPlaybackStarted ⇒ Event.PlaybackStarted
    case _: PbVenueTracksRemoved ⇒ Event.VenueTracksRemoved
    case a: PbVenueTrackAdded ⇒ Event.VenueTrackAdded(fromProtobuf(a.getTrack))
    case _: PbTrackFinished ⇒ Event.TrackFinished
    case a: PbUserTrackAdded ⇒
      Event.UserTrackAdded(fromProtobuf(a.getCoinToken), fromProtobuf(a.getTrackIdentifier))
    case _: PbCurrentTrackSkipped ⇒ Event.CurrentTrackSkipped
  }

  private def fromProtobuf(playlist: PbPlaylist): Playlist = {
    Playlist(fromProtobuf(playlist.getPlaylistInfo), playlist.tracks.map(fromProtobuf))
  }

  private def fromProtobuf(info: PbPlaylistInfo): PlaylistInfo = {
    val name = Name(info.getName.value)
    val identifier = fromProtobuf(info.getIdentifier)
    PlaylistInfo(name, identifier, info.images.map(fromProtobuf))
  }

  private def fromProtobuf(contentIdentifier: PbPlaylistIdentifier): PlaylistIdentifier = {
    contentIdentifier.contentProvider match {
      case CONTENT_PROVIDER_SPOTIFY ⇒
        PlaylistUri.fromString(contentIdentifier.getIdentifier.value).get
      case _ ⇒
        throw InvalidProviderIdentifier(contentIdentifier.getIdentifier.value)
    }
  }

  private def fromProtobuf(contentIdentifier: PbTrackIdentifier): TrackIdentifier = {
    contentIdentifier.contentProvider match {
      case CONTENT_PROVIDER_SPOTIFY ⇒ TrackUri(contentIdentifier.getIdentifier.value)
      case _ ⇒ throw InvalidProviderIdentifier(contentIdentifier.toString)
    }
  }

  private def fromProtobuf(image: PbImage): Image = {
    val height = if (image.height == DEFAULT_INT) None else Some(image.height)
    val width = if (image.width == DEFAULT_INT) None else Some(image.width)
    Image(image.url, height, width)
  }

  private def fromProtobuf(track: PbTrack): Track = {
    val name = Name(track.getName.value)
    Track(name, fromProtobuf(track.getIdentifier), fromProtobuf(track.getAlbum))
  }

  private def fromProtobuf(album: PbAlbum): Album = {
    val name = Name(album.getName.value)
    val artists = album.artists.map(fromProtobuf)
    val images = album.images.map(fromProtobuf)
    Album(name, artists, images)
  }

  private def fromProtobuf(artist: PbArtist): Artist = {
    Artist(Name(artist.getName.value))
  }

  private def fromProtobuf(coin: PbCoinToken): CoinToken = {
    CoinToken(Uid(coin.getVenueUid.value), coin.token)
  }
}
