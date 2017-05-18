package com.amplify.api.services.converters

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models._

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb): UnauthenticatedVenue = {
    UnauthenticatedVenue(venueDb.id, venueDb.name)
  }

  def venueDbToAuthenticatedVenue(
      venueDb: VenueDb,
      user: AuthenticatedUser): AuthenticatedVenue = {
    AuthenticatedVenue(venueDb.id, user, venueDb.name)
  }

  def venueReqToVenueDb(venueReq: VenueReq, userId: Id[User]): VenueDb = {
    VenueDb(name = venueReq.name, userId = userId, currentPlaylist = None)
  }
}
