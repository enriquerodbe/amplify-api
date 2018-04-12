package com.amplify.api.domain.models

object ContentType extends Enumeration {
  type ContentType = Value

  val Playlist = Value(1, "playlist")
  val Track = Value(2, "track")
}
