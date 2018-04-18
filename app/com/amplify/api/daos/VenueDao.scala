package com.amplify.api.daos

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
trait VenueDao {

  def retrieve(uid: Uid): DBIO[Option[DbVenue]]

  def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[DbVenue]]

  def retrieveOrCreate(dbVenue: DbVenue): DBIO[DbVenue]
}
