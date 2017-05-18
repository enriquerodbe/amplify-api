package com.amplify.api.exceptions

import com.amplify.api.domain.models.{ContentProviderIdentifier, Venue}

trait VenueException

case class UserAlreadyHasVenue(venue: Venue)
  extends BadRequestException(
    AppExceptionCode.UserAlreadyHasVenue,
    s"User already registered a venue: ${venue.name}")

case class VenueNotFound(userIdentifier: ContentProviderIdentifier)
  extends BadRequestException(
      AppExceptionCode.VenueNotFound,
      s"Venue not found for user identifier $userIdentifier")
