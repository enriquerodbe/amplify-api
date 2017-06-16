package com.amplify.api.exceptions

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{ContentProviderIdentifier, Venue}

trait VenueException

case class UserAlreadyHasVenue(venue: Venue)
  extends BadRequestException(
    AppExceptionCode.UserAlreadyHasVenue,
    s"User already registered a venue: ${venue.name}")

case class VenueNotFoundByUserIdentifier(userIdentifier: ContentProviderIdentifier)
  extends UnauthorizedException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found for user identifier $userIdentifier")

case class VenueNotFoundByUid(uid: Uid)
  extends BadRequestException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found with UID $uid")

case class CurrentPlaylistNotSet(venueUid: Uid)
  extends BadRequestException(
    AppExceptionCode.VenueCurrentPlaylistNotSet,
    s"Current playlist not set for venue $venueUid")
