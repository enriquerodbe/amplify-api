package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.{Code, Uid}
import com.amplify.api.domain.models.{PlaylistIdentifier, TrackIdentifier}

sealed trait Command {

  def venueUid: Uid
}

object Command {

  case class SetCurrentPlaylist(venueUid: Uid, playlist: PlaylistIdentifier) extends Command

  case class StartPlayback(venueUid: Uid) extends Command

  case class SkipCurrentTrack(venueUid: Uid) extends Command

  case class FinishCurrentTrack(venueUid: Uid) extends Command

  case class AddTrack(venueUid: Uid, code: Code, trackIdentifier: TrackIdentifier)
    extends Command
}
