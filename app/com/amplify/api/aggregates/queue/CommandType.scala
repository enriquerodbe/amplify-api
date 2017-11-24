package com.amplify.api.aggregates.queue

object CommandType extends Enumeration {

  type QueueCommandType = Value

  val
  SetCurrentPlaylist,
  StartPlayback,
  PausePlayback,
  SkipCurrentTrack,
  AddTrack
  = Value
}
