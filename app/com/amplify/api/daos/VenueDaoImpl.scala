package com.amplify.api.daos

import com.amplify.api.daos.models.DbVenue
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.Uid
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext)
  extends VenueDao with VenuesTable {

  import profile.api._

  override def retrieve(uid: Uid): DBIO[Option[DbVenue]] = {
    venuesTable.filter(_.uid === uid).result.headOption
  }

  override def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[DbVenue]] = {
    val query = venuesTable.filter { venue ⇒
      venue.authIdentifier === identifier.identifier &&
        venue.authProviderType === identifier.authProvider
    }

    query.result.headOption
  }

  override def retrieveOrCreate(dbVenue: DbVenue): DBIO[DbVenue] = {
    val maybeExistingVenue = retrieve(dbVenue.identifier)
    maybeExistingVenue.flatMap {
      case Some(venue) ⇒ DBIO.successful(venue)
      case _ ⇒ create(dbVenue)
    }
  }

  private def create(dbVenue: DbVenue): DBIO[DbVenue] = insertVenuesQuery += dbVenue
}
