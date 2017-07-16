package com.amplify.api.domain.models

object QueueEventType extends Enumeration {

  type QueueEventType = Value

  val
    VenueTracksRemoved,
    VenueTrackAdded,
    PlaybackStarted,
    AllTracksRemoved,
    TrackFinished,
    CurrentPlaylistSet,
    UserTrackAdded,
    CurrentTrackSkipped
  = Value
}
