package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Identifier

case class Playlist(source: PlaylistSource, identifier: Identifier[Playlist])

sealed trait PlaylistSource {

  case object SpotifyUri extends PlaylistSource
}
