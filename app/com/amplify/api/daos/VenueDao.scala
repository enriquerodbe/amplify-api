package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models.primitives.{Id, Token, Uid}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
trait VenueDao {

  def retrieve(uid: Uid): DBIO[Option[VenueDb]]

  def create(venueDb: VenueDb): DBIO[VenueDb]

  def updateFcmToken(id: Id, token: Token): DBIO[Unit]

  def retrieve(userId: Id): DBIO[Option[VenueDb]]

  def retrieveAllVenues(): DBIO[Seq[VenueDb]]
}
