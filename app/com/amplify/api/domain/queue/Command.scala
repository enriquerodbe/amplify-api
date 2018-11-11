package com.amplify.api.domain.queue

import com.amplify.api.domain.models.{CoinToken, Playlist, TrackIdentifier, Venue}

sealed trait Command {

  def venue: Venue
}

object Command {

  case class SetCurrentPlaylist(venue: Venue, playlist: Playlist) extends Command

  case class StartPlayback(venue: Venue) extends Command

  case class SkipCurrentTrack(venue: Venue) extends Command

  case class FinishCurrentTrack(venue: Venue) extends Command

  case class AddTrack(venue: Venue, coinToken: CoinToken, trackIdentifier: TrackIdentifier)
    extends Command
}
