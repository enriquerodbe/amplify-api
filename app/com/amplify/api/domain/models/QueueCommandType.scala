package com.amplify.api.domain.models

object QueueCommandType extends Enumeration {

  type QueueCommandType = Value

  val
    SetCurrentPlaylist,
    AddTrack,
    StartPlayback,
    PausePlayback,
    SkipCurrentTrack,
    StartAmplifying,
    StopAmplifying,
    FinishTrack,
    AddUserTrack
  = Value
}
