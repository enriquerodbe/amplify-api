package com.amplify.api.exceptions

import com.amplify.api.domain.models.primitives.Uid

trait VenueException

case class VenueNotFoundByUid(uid: Uid)
  extends BadRequestException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found with UID $uid")
    with VenueException
