package com.amplify.api.aggregates.queue.daos

object CommandType extends Enumeration {

  type CommandType = Value

  val
  SetCurrentPlaylist,
  StartPlayback,
  PausePlayback,
  SkipCurrentTrack,
  AddTrack
  = Value
}
