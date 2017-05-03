package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.domain.models.primitives.Name
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
trait VenueDao {

  def create(user: UserDb, name: Name): DBIO[VenueDb]

  def retrieve(user: UserDb): DBIO[Option[VenueDb]]
}
