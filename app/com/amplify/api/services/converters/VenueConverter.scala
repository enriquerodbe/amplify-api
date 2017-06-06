package com.amplify.api.services.converters

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb): UnauthenticatedVenue = {
    UnauthenticatedVenue(venueDb.id, venueDb.name, venueDb.uid)
  }

  def venueDbToAuthenticatedVenue(
      venueDb: VenueDb,
      user: AuthenticatedUser): AuthenticatedVenue = {
    AuthenticatedVenue(user, venueDbToVenue(venueDb))
  }

  def venueReqToVenueDb(venueReq: VenueRequest, userId: Id[User]): VenueDb = {
    VenueDb(name = venueReq.name, userId = userId, uid = Uid())
  }
}
