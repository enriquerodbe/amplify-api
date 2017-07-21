package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class Playlist(info: PlaylistInfo, tracks: Seq[Track]) {

  def findTrack(identifier: ContentProviderIdentifier): Option[Track] = {
    tracks.find(_.identifier == identifier)
  }
}

case class PlaylistInfo(name: Name, identifier: ContentProviderIdentifier, images: Seq[Image])
