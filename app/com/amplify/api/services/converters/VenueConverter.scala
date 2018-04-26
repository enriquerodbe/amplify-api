package com.amplify.api.services.converters

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Token, Uid}
import com.amplify.api.services.models.UserData

object VenueConverter {

  def dbVenueToVenue(dbVenue: DbVenue): Venue = {
    Venue(dbVenue.name, dbVenue.uid, dbVenue.identifier, dbVenue.accessToken)
  }

  def userDataToDbVenue(
      userData: UserData,
      refreshToken: Token,
      accessToken: Token): DbVenue = {
    DbVenue(
      name = userData.name,
      uid = Uid(),
      identifier = userData.identifier,
      refreshToken = refreshToken,
      accessToken = accessToken)
  }
}
