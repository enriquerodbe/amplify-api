package com.amplify.api.aggregates.queue.daos

import com.amplify.api.domain.models.{AuthProviderIdentifier, ContentProviderIdentifier}

sealed trait EventDbData

object EventDbData {

  case class CurrentPlaylistSet(playlistIdentifier: ContentProviderIdentifier) extends EventDbData

  case object VenueTracksRemoved extends EventDbData

  case class VenueTrackAdded(trackIdentifier: ContentProviderIdentifier) extends EventDbData

  case object TrackFinished extends EventDbData

  case class UserTrackAdded(
      userIdentifier: AuthProviderIdentifier,
      trackIdentifier: ContentProviderIdentifier) extends EventDbData

  case object TrackSkipped extends EventDbData
}
