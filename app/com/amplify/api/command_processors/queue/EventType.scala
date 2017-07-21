package com.amplify.api.command_processors.queue

object EventType extends Enumeration {

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
