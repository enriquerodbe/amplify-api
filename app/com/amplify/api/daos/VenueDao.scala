package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.User
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[VenueDaoImpl])
trait VenueDao {

  def create(venueDb: VenueDb): DBIO[VenueDb]

  def retrieve(userId: Id[User]): DBIO[Option[VenueDb]]

  def retrieveAllVenues(): DBIO[Seq[VenueDb]]
}
