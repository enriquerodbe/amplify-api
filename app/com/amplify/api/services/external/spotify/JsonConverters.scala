package com.amplify.api.services.external.spotify

import com.amplify.api.services.external.spotify.Dtos._
import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonConverters {

  implicit val userReads = JsonNaming.snakecase(Json.reads[User])

  implicit val playlistReads = JsonNaming.snakecase(Json.reads[Playlist])

  implicit val playlistsReads = JsonNaming.snakecase(Json.reads[Playlists])

  implicit val trackFormat = JsonNaming.snakecase(Json.format[Track])

  implicit val trackItemFormat = JsonNaming.snakecase(Json.format[TrackItem])

  implicit def pageFormat[T: Format]: Format[Page[T]] = {
    ((__ \ "items").format[Seq[T]] ~
      (__ \ "total").format[Int] ~
      (__ \ "next").formatNullable[String])(Page.apply, unlift(Page.unapply[T]))
  }
}
