package com.amplify.api.domain.models

object EventSourceType extends Enumeration {

  type EventSourceType = Value

  val
    SetCurrentPlaylist,
    AddTrack,
    StartPlayback,
    PausePlayback,
    SkipCurrentTrack,
    StartAmplifying,
    StopAmplifying,
    TrackFinished,
    AddUserTrack
  = Value
}
