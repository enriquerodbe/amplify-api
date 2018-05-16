package com.amplify.api.services.converters

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid

object VenueConverter {

  def dbVenueToVenue(dbVenue: DbVenue): Venue = {
    Venue(dbVenue.name, dbVenue.uid, dbVenue.identifier, dbVenue.refreshToken, dbVenue.accessToken)
  }

  def userDataToDbVenue(venueData: VenueData): DbVenue = {
    DbVenue(
      name = venueData.name,
      uid = Uid(),
      identifier = venueData.identifier,
      refreshToken = venueData.refreshToken,
      accessToken = venueData.accessToken)
  }
}
