package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.Command._
import com.amplify.api.aggregates.queue.daos.{CommandDb, CommandDbData}
import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

object CommandConverter {

  def commandToCommandDb(command: Command, venueId: Id): CommandDb = {
    CommandDb(
      venueId = venueId,
      command = toCommandDbData(command),
      createdAt = Instant.now())
  }

  private def toCommandDbData(command: Command) = command match {
    case SetCurrentPlaylist(venue, playlist) ⇒
      CommandDbData.SetCurrentPlaylist(venue.uid, playlist.info.identifier)
    case StartPlayback(venue) ⇒ CommandDbData.StartPlayback(venue.uid)
    case PausePlayback(venue) ⇒ CommandDbData.PausePlayback(venue.uid)
    case SkipCurrentTrack(venue) ⇒ CommandDbData.SkipCurrentTrack(venue.uid)
    case AddTrack(venue, user, trackIdentifier) ⇒
      CommandDbData.AddTrack(venue.uid, user.identifier, trackIdentifier)
  }
}
