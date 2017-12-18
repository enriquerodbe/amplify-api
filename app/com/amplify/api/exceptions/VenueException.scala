package com.amplify.api.exceptions

import com.amplify.api.domain.models.primitives.{Id, Uid}
import com.amplify.api.domain.models.{ContentProviderIdentifier, Venue}

trait VenueException

case class UserAlreadyHasVenue(venue: Venue)
  extends BadRequestException(
    AppExceptionCode.UserAlreadyHasVenue,
    s"User already registered a venue: ${venue.name}")
    with VenueException

case class VenueNotFoundByUserIdentifier(userIdentifier: ContentProviderIdentifier)
  extends UnauthorizedException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found for user identifier $userIdentifier")
    with VenueException

case class VenueNotFoundByUid(uid: Uid)
  extends BadRequestException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found with UID $uid")
    with VenueException

case class VenueNotFoundById(id: Id)
  extends BadRequestException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found with ID $id")
    with VenueException
