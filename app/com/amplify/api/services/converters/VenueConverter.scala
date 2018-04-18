package com.amplify.api.services.converters

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.services.models.UserData

object VenueConverter {

  def dbVenueToVenue(dbVenue: DbVenue): Venue = {
    Venue(dbVenue.name, dbVenue.uid, dbVenue.identifier)
  }

  def venueReqToDbVenue(name: Name, identifier: AuthProviderIdentifier): DbVenue = {
    DbVenue(name = name, uid = Uid(), identifier = identifier)
  }

  def userDataToDbVenue(userData: UserData, name: Name): DbVenue = {
    DbVenue(name = name, uid = Uid(), identifier = userData.identifier)
  }
}
