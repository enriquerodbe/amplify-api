package com.amplify.api.exceptions

import com.amplify.api.domain.models.AuthenticatedVenue

trait VenueException

case class UserAlreadyHasVenue(venue: AuthenticatedVenue)
  extends BadRequestException(
    AppExceptionCode.UserAlreadyHasVenue,
    s"User already registered a venue: ${venue.name}")
