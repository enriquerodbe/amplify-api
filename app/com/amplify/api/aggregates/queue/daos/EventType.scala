package com.amplify.api.aggregates.queue.daos

object EventType extends Enumeration {

  type EventType = Value

  val
    VenueTracksRemoved,
    VenueTrackAdded,
    TrackFinished,
    CurrentPlaylistSet,
    UserTrackAdded,
    TrackSkipped
  = Value
}
