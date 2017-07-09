package com.amplify.api.exceptions

import com.amplify.api.domain.models.primitives.Uid

trait QueueException

case class CurrentPlaylistNotSet(venueUid: Uid)
  extends BadRequestException(
    AppExceptionCode.VenueCurrentPlaylistNotSet,
    s"Current playlist not set for venue $venueUid")
    with QueueException
