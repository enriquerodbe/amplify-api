package com.amplify.api.services.external.spotify

object Dtos {

  case class User(id: String, displayName: String, email: String)

  case class Playlists(items: Seq[PlaylistItem])

  case class PlaylistItem(id: String, name: String)
}
