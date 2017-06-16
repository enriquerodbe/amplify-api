package com.amplify.api.services.external.spotify

import com.amplify.api.services.external.spotify.Dtos._
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonConverters {

  implicit val userReads = JsonNaming.snakecase(Json.format[User])

  implicit val imageReads = JsonNaming.snakecase(Json.format[Image])

  implicit val playlistReads = JsonNaming.snakecase(Json.format[Playlist])

  implicit val playlistsReads = JsonNaming.snakecase(Json.format[Playlists])

  implicit val artistFormat = JsonNaming.snakecase(Json.format[Artist])

  implicit val albumFormat = JsonNaming.snakecase(Json.format[Album])

  implicit val trackFormat = JsonNaming.snakecase(Json.format[Track])

  implicit val trackItemFormat = JsonNaming.snakecase(Json.format[TrackItem])

  implicit def pageFormat[T: Format]: Format[Page[T]] = {
    ((__ \ "items").format[Seq[T]] ~
      (__ \ "total").format[Int] ~
      (__ \ "next").formatNullable[String])(Page.apply, unlift(Page.unapply[T]))
  }
}
