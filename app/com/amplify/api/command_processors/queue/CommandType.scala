package com.amplify.api.command_processors.queue

object CommandType extends Enumeration {

  type QueueCommandType = Value

  val
  SetCurrentPlaylist,
  StartPlayback,
  PausePlayback,
  SkipCurrentTrack
  = Value
}
