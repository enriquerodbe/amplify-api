package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.User
import com.amplify.api.domain.models.primitives.Uid
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext)
  extends VenueDao with VenuesTable {

  import profile.api._

  override def retrieve(uid: Uid): DBIO[Option[VenueDb]] = {
    venuesTable.filter(_.uid === uid).result.headOption
  }

  override def create(venueDb: VenueDb): DBIO[VenueDb] = {
    (venuesTable returning venuesTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) += venueDb
  }

  override def retrieve(userId: Id[User]): DBIO[Option[VenueDb]] = {
    venuesTable.filter(_.userId === userId).result.headOption
  }

  override def retrieveAllVenues(): DBIO[Seq[VenueDb]] = {
    venuesTable.result
  }
}
