package com.amplify.api.domain.models

import com.amplify.api.domain.models.QueueCommandType.QueueCommandType

sealed trait QueueCommand {

  def venue: AuthenticatedVenue

  def user: Option[User] = None

  def queueCommandType: QueueCommandType

  def contentIdentifier: Option[ContentProviderIdentifier] = None
}

object QueueCommand {

  case class SetCurrentPlaylist(
      venue: AuthenticatedVenue,
      playlistIdentifier: ContentProviderIdentifier) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.SetCurrentPlaylist

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(playlistIdentifier)
  }

  case class StartPlayback(venue: AuthenticatedVenue) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.StartPlayback
  }

  case class PausePlayback(venue: AuthenticatedVenue) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.PausePlayback
  }

  case class SkipCurrentTrack(venue: AuthenticatedVenue) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.SkipCurrentTrack
  }

  case class StartAmplifying(venue: AuthenticatedVenue) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.StartAmplifying
  }

  case class StopAmplifying(venue: AuthenticatedVenue) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.StopAmplifying
  }

  case class FinishTrack(venue: AuthenticatedVenue) extends QueueCommand {

    override def queueCommandType: QueueCommandType = QueueCommandType.FinishTrack
  }

  case class AddUserTrack(
      venue: AuthenticatedVenue,
      authenticatedUser: AuthenticatedUser,
      trackIdentifier: ContentProviderIdentifier) extends QueueCommand {

    override def user: Option[User] = Some(authenticatedUser)

    override def queueCommandType: QueueCommandType = QueueCommandType.AddUserTrack

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(trackIdentifier)
  }
}
