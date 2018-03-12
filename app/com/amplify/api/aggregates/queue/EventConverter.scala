package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.Event._
import com.amplify.api.aggregates.queue.daos.{CommandDb, EventDb, EventDbData}
import java.time.Instant

object EventConverter {

  def queueEventToQueueEventDb(
      queueCommand: CommandDb,
      queueEvent: Event): EventDb = {
    EventDb(
      queueCommandId = queueCommand.id,
      event = toEventDbData(queueEvent),
      createdAt = Instant.now())
  }

  private def toEventDbData(event: Event) = event match {
    case CurrentPlaylistSet(playlist) ⇒ EventDbData.CurrentPlaylistSet(playlist.info.identifier)
    case VenueTracksRemoved ⇒ EventDbData.VenueTracksRemoved
    case VenueTrackAdded(track) ⇒ EventDbData.VenueTrackAdded(track.identifier)
    case TrackFinished ⇒ EventDbData.TrackFinished
    case UserTrackAdded(user, trackIdentifier) ⇒
      EventDbData.UserTrackAdded(user.identifier, trackIdentifier)
    case CurrentTrackSkipped ⇒ EventDbData.TrackSkipped
  }
}
