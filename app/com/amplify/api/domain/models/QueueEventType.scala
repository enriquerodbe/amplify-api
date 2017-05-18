package com.amplify.api.domain.models

object QueueEventType extends Enumeration {

  type QueueEventType = Value

  val RemoveVenueTracks = Value(1)
  val AddVenueTrack = Value(2)
  val StartPlaying = Value(3)
}
