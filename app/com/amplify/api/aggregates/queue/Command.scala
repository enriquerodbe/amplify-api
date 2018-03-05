package com.amplify.api.aggregates.queue

import com.amplify.api.aggregates.queue.CommandType.QueueCommandType
import com.amplify.api.domain.models.{ContentProviderIdentifier, Playlist, User, Venue}


sealed trait Command {

  def venue: Venue

  def maybeUser: Option[User]

  def queueCommandType: QueueCommandType

  def contentIdentifier: Option[ContentProviderIdentifier] = None
}

object Command {

  case class SetCurrentPlaylist(venue: Venue, playlist: Playlist) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.SetCurrentPlaylist

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(playlist.info.identifier)
    }

    override def maybeUser: Option[User] = None
  }

  case class StartPlayback(venue: Venue) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.StartPlayback

    override def maybeUser: Option[User] = None
  }

  case class PausePlayback(venue: Venue) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.PausePlayback

    override def maybeUser: Option[User] = None
  }

  case class SkipCurrentTrack(venue: Venue) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.SkipCurrentTrack

    override def maybeUser: Option[User] = None
  }

  case class AddTrack(
      venue: Venue,
      user: User,
      trackIdentifier: ContentProviderIdentifier) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.AddTrack

    override def maybeUser: Option[User] = Some(user)
  }
}
