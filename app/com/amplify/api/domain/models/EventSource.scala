package com.amplify.api.domain.models

import com.amplify.api.domain.models.EventSourceType.EventSourceType

sealed trait EventSource {

  def venue: AuthenticatedVenue

  def user: Option[User] = None

  def eventType: EventSourceType

  def contentIdentifier: Option[ContentProviderIdentifier] = None
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
  }

  case class PausePlayback(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.PausePlayback
  }

  case class SkipCurrentTrack(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.SkipCurrentTrack
  }

  case class StartAmplifying(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.StartAmplifying
  }

  case class StopAmplifying(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.StopAmplifying
  }

  case class TrackFinished(venue: AuthenticatedVenue) extends EventSource {

    override def eventType: EventSourceType = EventSourceType.TrackFinished
  }

  case class AddUserTrack(
      venue: AuthenticatedVenue,
      authenticatedUser: AuthenticatedUser,
      trackIdentifier: ContentProviderIdentifier) extends EventSource {

    override def user: Option[User] = Some(authenticatedUser)

    override def eventType: EventSourceType = EventSourceType.AddUserTrack

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(trackIdentifier)
  }
}
