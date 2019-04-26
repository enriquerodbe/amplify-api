package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProvider.ContentProvider
import com.amplify.api.domain.models.ContentType.ContentType
import com.amplify.api.domain.models.Spotify.{PlaylistUri, TrackUri}
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
import scala.util.{Failure, Success, Try}

sealed trait ContentIdentifier {

  val contentProvider: ContentProvider
  val contentType: ContentType
  def identifier: Identifier

  override def toString: String = s"$contentProvider:$contentType:${identifier.value}"
}

object ContentIdentifier {

  private val regex = "(\\w+)?:(\\w+)?:(.+)".r

  def fromString(str: String): Try[ContentIdentifier] = str match {
    case regex(providerName, contentType, identifier)
      if providerName == ContentProvider.Spotify.toString &&
        contentType == ContentType.Playlist.toString ⇒
      PlaylistUri.fromString(identifier)
    case regex(providerName, contentType, identifier)
      if providerName == ContentProvider.Spotify.toString &&
        contentType == ContentType.Track.toString ⇒
      Success(TrackUri(identifier))
    case _ ⇒
      Failure(InvalidProviderIdentifier(str))
  }
}

sealed trait PlaylistIdentifier extends ContentIdentifier
object PlaylistIdentifier {

  def fromString(str: String): Try[PlaylistIdentifier] = {
    ContentIdentifier.fromString(str).flatMap {
      case playlistIdentifier: PlaylistIdentifier ⇒ Success(playlistIdentifier)
      case _ ⇒ Failure(InvalidProviderIdentifier(str))
    }
  }
}
sealed trait TrackIdentifier extends ContentIdentifier
object TrackIdentifier {

  def fromString(str: String): Try[TrackIdentifier] = {
    ContentIdentifier.fromString(str).flatMap {
      case trackIdentifier: TrackIdentifier ⇒ Success(trackIdentifier)
      case _ ⇒ Failure(InvalidProviderIdentifier(str))
    }
  }
}

object Spotify {

  case class PlaylistUri(owner: String, id: String) extends PlaylistIdentifier {

    override val contentProvider: ContentProvider = ContentProvider.Spotify

    override val contentType: ContentType = ContentType.Playlist

    override def identifier: Identifier = Identifier(s"$owner:$id")
  }

  object PlaylistUri {

    val regex = "(.+)?:(.+)".r

    def fromString(str: String): Try[PlaylistUri] = str match {
      case regex(owner, id) ⇒ Success(PlaylistUri(owner, id))
      case _ ⇒ Failure(InvalidProviderIdentifier(str))
    }
  }

  case class TrackUri(id: String) extends TrackIdentifier {

    override val contentProvider: ContentProvider = ContentProvider.Spotify

    override val contentType: ContentType = ContentType.Track

    override def identifier: Identifier = Identifier(id)
  }
}
