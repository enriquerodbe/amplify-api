package com.amplify.api.services.external.spotify

import com.amplify.api.services.external.spotify.Dtos._
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json._

object JsonConverters {

  implicit val config = JsonConfiguration(SnakeCase)

  implicit val userReads = Json.format[User]

  implicit val imageReads = Json.format[Image]

  implicit val playlistReads = Json.format[Playlist]

  implicit val playlistsReads = Json.format[Playlists]

  implicit val artistFormat = Json.format[Artist]

  implicit val albumFormat = Json.format[Album]

  implicit val trackFormat = Json.format[Track]

  implicit val trackItemFormat = Json.format[TrackItem]
}
