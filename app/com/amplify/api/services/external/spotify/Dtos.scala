package com.amplify.api.services.external.spotify

object Dtos {

  case class User(id: String, displayName: String)

  case class Playlists(items: Seq[Playlist])

  case class Playlist(id: String, name: String, images: Seq[Image])

  case class Image(url: String, height: Option[Int], width: Option[Int])

  case class TrackItem(track: Track)

  case class Track(id: String, name: String)

  case class Page[T](items: Seq[T], total: Int, next: Option[String])
}
