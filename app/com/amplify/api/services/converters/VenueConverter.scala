package com.amplify.api.services.converters

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Token, Uid}
import com.amplify.api.services.models.UserData

object VenueConverter {

  def dbVenueToVenue(dbVenue: DbVenue): Venue = {
    Venue(dbVenue.name, dbVenue.uid, dbVenue.identifier)
  }

  def userDataToDbVenue(
      name: Name,
      userData: UserData,
      refreshToken: Token,
      accessToken: Token): DbVenue = {
    DbVenue(
      name = name,
      uid = Uid(),
      identifier = userData.identifier,
      refreshToken = refreshToken,
      accessToken = accessToken)
  }
}
