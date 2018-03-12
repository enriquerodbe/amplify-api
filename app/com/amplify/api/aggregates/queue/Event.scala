package com.amplify.api.aggregates.queue

import com.amplify.api.domain.models._

sealed trait Event

object Event {

  case class CurrentPlaylistSet(playlist: Playlist) extends Event

  case object VenueTracksRemoved extends Event

  case class VenueTrackAdded(track: Track) extends Event

  case object TrackFinished extends Event

  case class UserTrackAdded(user: User, trackIdentifier: ContentProviderIdentifier) extends Event

  case object CurrentTrackSkipped extends Event
}
