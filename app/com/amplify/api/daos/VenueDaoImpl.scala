package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Token, Uid}
import com.amplify.api.exceptions.VenueNotFoundByUid
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext)
  extends VenueDao with VenuesTable {

  import profile.api._

  override def retrieveAll(): DBIO[Seq[VenueDb]] = venuesTable.result

  override def retrieve(uid: Uid): DBIO[Option[VenueDb]] = {
    venuesTable.filter(_.uid === uid).result.headOption
  }

  override def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[VenueDb]] = {
    val query = venuesTable.filter { venue ⇒
      venue.authIdentifier === identifier.identifier &&
        venue.authProviderType === identifier.authProvider
    }

    query.result.headOption
  }

  override def create(venueDb: VenueDb): DBIO[VenueDb] = {
    (venuesTable returning venuesTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) += venueDb
  }

  override def retrieveOrCreate(venueDb: VenueDb): DBIO[VenueDb] = {
    val maybeExistingVenue = retrieve(venueDb.identifier)
    maybeExistingVenue.flatMap {
      case Some(venue) ⇒ DBIO.successful(venue)
      case _ ⇒ create(venueDb)
    }
  }

  override def updateFcmToken(uid: Uid, token: Token): DBIO[Unit] = {
    venuesTable
      .filter(_.uid === uid)
      .map(_.fcmToken)
      .update(Some(token))
      .flatMap {
        case n if n == 1 ⇒ DBIO.successful(())
        case _ ⇒ DBIO.failed(VenueNotFoundByUid(uid))
      }
  }
}
