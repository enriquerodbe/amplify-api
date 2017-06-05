package com.amplify.api.domain.models

import com.amplify.api.domain.models.EventSourceType.EventSourceType

sealed trait EventSource {

  def venue: AuthenticatedVenue

  def eventType: EventSourceType

  def contentIdentifier: Option[ContentProviderIdentifier]
}

object EventSource {

  case class SetCurrentPlaylist(
      venue: AuthenticatedVenue,
      playlistIdentifier: ContentProviderIdentifier) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.SetCurrentPlaylist

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(playlistIdentifier)
  }

  case class StartPlayback(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.StartPlayback

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class PausePlayback(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.PausePlayback

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class StartAmplifying(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.StartAmplifying

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class StopAmplifying(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.StopAmplifying

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class TrackFinished(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.TrackFinished

    override def contentIdentifier: Option[ContentProviderIdentifier] = None
  }
}
