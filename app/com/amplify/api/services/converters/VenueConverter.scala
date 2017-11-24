package com.amplify.api.services.converters

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Id, Name, Uid}

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb): UnauthenticatedVenue = {
    UnauthenticatedVenue(venueDb.id, venueDb.name, venueDb.uid, venueDb.fcmToken)
  }

  def venueDbToAuthenticatedVenue(
      venueDb: VenueDb,
      user: AuthenticatedUser): AuthenticatedVenue = {
    AuthenticatedVenue(user, venueDbToVenue(venueDb))
  }

  def venueReqToVenueDb(name: Name, userId: Id): VenueDb = {
    VenueDb(name = name, userId = userId, uid = Uid(), fcmToken = None)
  }
}
