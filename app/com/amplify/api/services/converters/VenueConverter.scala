package com.amplify.api.services.converters

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models.Venue

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb): Venue = Venue(venueDb.name, null)
}
