package com.amplify.api.aggregates.queue.daos

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{AuthProviderIdentifier, ContentProviderIdentifier}

sealed trait CommandDbData {

  val venueUid: Uid
}

object CommandDbData {

  case class SetCurrentPlaylist(
      venueUid: Uid,
      playlistIdentifier: ContentProviderIdentifier) extends CommandDbData

  case class StartPlayback(venueUid: Uid) extends CommandDbData

  case class PausePlayback(venueUid: Uid) extends CommandDbData

  case class SkipCurrentTrack(venueUid: Uid) extends CommandDbData

  case class AddTrack(
      venueUid: Uid,
      userIdentifier: AuthProviderIdentifier,
      trackIdentifier: ContentProviderIdentifier) extends CommandDbData
}
