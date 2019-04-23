package com.amplify.api.domain.queue

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{CoinCode, PlaylistIdentifier, TrackIdentifier}

sealed trait Command {

  def venueUid: Uid
}

object Command {

  case class SetCurrentPlaylist(venueUid: Uid, playlist: PlaylistIdentifier) extends Command

  case class StartPlayback(venueUid: Uid) extends Command

  case class SkipCurrentTrack(venueUid: Uid) extends Command

  case class FinishCurrentTrack(venueUid: Uid) extends Command

  case class AddTrack(venueUid: Uid, coinCode: CoinCode, trackIdentifier: TrackIdentifier)
    extends Command
}
