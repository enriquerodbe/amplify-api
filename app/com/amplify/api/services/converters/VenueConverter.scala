package com.amplify.api.services.converters

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.services.models.UserData

object VenueConverter {

  def venueDbToVenue(venueDb: VenueDb): Venue = {
    Venue(venueDb.name, venueDb.uid, venueDb.identifier)
  }

  def venueReqToVenueDb(name: Name, identifier: AuthProviderIdentifier): VenueDb = {
    VenueDb(name = name, uid = Uid(), identifier = identifier)
  }

  def userDataToVenueDb(userData: UserData, name: Name): VenueDb = {
    VenueDb(name = name, uid = Uid(), identifier = userData.identifier)
  }
}
