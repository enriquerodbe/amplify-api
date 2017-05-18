package com.amplify.api.domain.models

sealed trait EventSource

object EventSource {

  case class SetCurrentPlaylist(
      venue: AuthenticatedVenue,
      playlistIdentifier: ContentProviderIdentifier) extends EventSource

  case class StartPlaying(venue: AuthenticatedVenue) extends EventSource
}
