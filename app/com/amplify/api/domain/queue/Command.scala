package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.domain.models.{PlaylistIdentifier, TrackIdentifier}

sealed trait Command {

  def venueUid: Uid
}

object Command {

  case class SetAllowedPlaylist(venueUid: Uid, playlist: PlaylistIdentifier) extends Command

  case class StartPlayback(venueUid: Uid) extends Command

  case class SkipCurrentTrack(venueUid: Uid) extends Command

  case class FinishCurrentTrack(venueUid: Uid) extends Command

  case class AddPlaylistTracks(venueUid: Uid, playlistIdentifier: PlaylistIdentifier)
      extends Command

  case class AddVenueTrack(venueUid: Uid, trackIdentifier: TrackIdentifier) extends Command

  case class AddTrack(venueUid: Uid, code: Code, trackIdentifier: TrackIdentifier)
    extends Command
}
