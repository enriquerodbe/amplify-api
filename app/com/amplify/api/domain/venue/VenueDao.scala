package com.amplify.api.domain.venue

import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.{Access, Token, Uid}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
private trait VenueDao {

  def retrieve(uid: Uid): DBIO[Option[DbVenue]]

  def retrieveOrCreate(dbVenue: DbVenue): DBIO[DbVenue]

  def updateAccessToken(venue: Venue, accessToken: Token[Access]): DBIO[Unit]
}
