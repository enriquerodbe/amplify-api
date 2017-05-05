package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.User
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider) extends VenueDao with VenuesTable {

  import profile.api._

  override def create(venueDb: VenueDb): DBIO[VenueDb] = {
    (venuesTable returning venuesTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) += venueDb
  }

  override def retrieve(userId: Id[User]): DBIO[Option[VenueDb]] = {
    venuesTable.filter(_.userId === userId).take(1).result.headOption
  }
}
