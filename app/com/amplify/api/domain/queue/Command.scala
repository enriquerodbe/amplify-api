package com.amplify.api.domain.queue

import com.amplify.api.domain.models.{CoinCode, PlaylistIdentifier, TrackIdentifier, Venue}

sealed trait Command {

  def venue: Venue
}

object Command {

  case class SetCurrentPlaylist(venue: Venue, playlist: PlaylistIdentifier) extends Command

  case class StartPlayback(venue: Venue) extends Command

  case class SkipCurrentTrack(venue: Venue) extends Command

  case class FinishCurrentTrack(venue: Venue) extends Command

  case class AddTrack(venue: Venue, coinCode: CoinCode, trackIdentifier: TrackIdentifier)
    extends Command
}
