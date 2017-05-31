package com.amplify.api.domain.models

sealed trait EventSource

object EventSource {

  case class SetCurrentPlaylist(
      venue: AuthenticatedVenue,
      playlistIdentifier: ContentProviderIdentifier) extends EventSource

  case class StartPlayback(venue: AuthenticatedVenue) extends EventSource

  case class PausePlayback(venue: AuthenticatedVenue) extends EventSource
}
