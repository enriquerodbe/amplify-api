package com.amplify.api.domain.models

object QueueEventType extends Enumeration {

  type QueueEventType = Value

  val
    RemoveVenueTracks,
    AddVenueTrack,
    StartPlaying,
    RemoveAllTracks
  = Value
}
