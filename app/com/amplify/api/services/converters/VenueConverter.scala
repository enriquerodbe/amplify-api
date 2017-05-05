package com.amplify.api.services.converters

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{AuthenticatedUser, AuthenticatedVenue, User, VenueReq}

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb, user: AuthenticatedUser): AuthenticatedVenue = {
    AuthenticatedVenue(user, venueDb.name)
  }

  def venueReqToVenueDb(venueReq: VenueReq, userId: Id[User]): VenueDb = {
    VenueDb(name = venueReq.name, userId = userId)
  }
}
