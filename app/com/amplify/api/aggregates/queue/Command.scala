package com.amplify.api.aggregates.queue

import com.amplify.api.domain.models._

sealed trait Command {

  def venue: Venue
}

object Command {

  case class SetCurrentPlaylist(venue: Venue, playlist: Playlist) extends Command

  case class SkipCurrentTrack(venue: Venue) extends Command

  case class FinishCurrentTrack(venue: Venue) extends Command

  case class AddTrack(
      venue: Venue,
      user: User,
      trackIdentifier: ContentProviderIdentifier) extends Command
}
