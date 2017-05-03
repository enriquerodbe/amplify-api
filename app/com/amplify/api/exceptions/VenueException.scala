package com.amplify.api.exceptions

import com.amplify.api.domain.models.Venue

trait VenueException

case class UserAlreadyHasVenue(venue: Venue)
  extends BadRequestException(
    AppExceptionCode.UserAlreadyHasVenue,
    s"User already registered a venue: ${venue.name}")
