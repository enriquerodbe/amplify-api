package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Token, Uid}
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
trait VenueDao {

  def retrieveAll(): DBIO[Seq[VenueDb]]

  def retrieve(uid: Uid): DBIO[Option[VenueDb]]

  def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[VenueDb]]

  def retrieveOrCreate(venueDb: VenueDb): DBIO[VenueDb]
}
