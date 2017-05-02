package com.amplify.api.services.converters

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models.{AuthenticatedUser, Venue}

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb, user: AuthenticatedUser): Venue = Venue(user, venueDb.name)
}
