package com.amplify.api.domain.venue

import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.models.{Venue, VenueData}

private object VenueConverter {

  def dbVenueToVenue(dbVenue: DbVenue): Venue = {
    Venue(dbVenue.name, dbVenue.uid, dbVenue.identifier, dbVenue.refreshToken, dbVenue.accessToken)
  }

  def venueDataToDbVenue(venueData: VenueData): DbVenue = {
    DbVenue(
      name = venueData.name,
      uid = Uid(),
      identifier = venueData.identifier,
      refreshToken = venueData.refreshToken,
      accessToken = venueData.accessToken)
  }
}
