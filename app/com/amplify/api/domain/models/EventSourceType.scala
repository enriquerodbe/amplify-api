package com.amplify.api.domain.models

object EventSourceType extends Enumeration {

  type EventSourceType = Value

  val SetCurrentPlaylist = Value(1)
  val AddTrack = Value(2)
  val StartPlaying = Value(3)
}
