package com.amplify.api.aggregates.queue

object EventType extends Enumeration {

  type QueueEventType = Value

  val
    VenueTracksRemoved,
    VenueTrackAdded,
    AllTracksRemoved,
    TrackFinished,
    CurrentPlaylistSet,
    UserTrackAdded,
    CurrentTrackSkipped
  = Value
}
