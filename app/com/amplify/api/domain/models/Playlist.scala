package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name
import scala.util.{Failure, Success, Try}

case class Playlist(identifier: PlaylistInfo, tracks: Seq[Track]) {

  def findTrack(identifier: ContentProviderIdentifier): Option[Track] = {
    tracks.find(_.identifier == identifier)
  }

  def getTrack(identifier: ContentProviderIdentifier): Try[Track] = {
    findTrack(identifier) match {
      case Some(track) ⇒ Success(track)
      case _ ⇒ Failure(new Exception)
    }
  }
}

case class PlaylistInfo(name: Name, identifier: ContentProviderIdentifier, images: Seq[Image])
