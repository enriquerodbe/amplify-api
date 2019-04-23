package com.amplify.api.domain.venue

import com.amplify.api.domain.models.primitives.{Access, Token, Uid}
import com.amplify.api.domain.models.{AuthProviderIdentifier, Venue}
import com.amplify.api.shared.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.DbioUtils._
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

private class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext)
  extends VenueDao with VenuesTable {

  import profile.api._

  override def retrieve(uid: Uid): DBIO[Option[DbVenue]] = {
    venuesTable.filter(_.uid === uid).result.headOption
  }

  override def retrieveOrCreate(dbVenue: DbVenue): DBIO[DbVenue] = {
    val maybeExistingVenue = retrieve(dbVenue.identifier)
    maybeExistingVenue.flatMap {
      case Some(venue) ⇒
        val updated =
          venue.copy(refreshToken = dbVenue.refreshToken, accessToken = dbVenue.accessToken)
        updateTokens(updated)

      case _ ⇒ create(dbVenue)
    }
  }

  private def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[DbVenue]] = {
    val query = venuesTable.filter { venue ⇒
      venue.authIdentifier === identifier.identifier &&
        venue.authProviderType === identifier.authProvider
    }
    query.result.headOption
  }

  private def updateTokens(dbVenue: DbVenue): DBIO[DbVenue] = {
    venuesTable
        .filter(_.uid === dbVenue.uid)
        .map(r ⇒ r.refreshToken → r.accessToken)
        .update(dbVenue.refreshToken → dbVenue.accessToken)
        .map(_ ⇒ dbVenue)
  }

  private def create(dbVenue: DbVenue): DBIO[DbVenue] = insertVenuesQuery += dbVenue

  override def updateAccessToken(venue: Venue, accessToken: Token[Access]): DBIO[Unit] = {
    for {
      dbVenue ← retrieve(venue.uid) ?! VenueNotFoundByUid(venue.uid)
      _ ← updateTokens(dbVenue.copy(accessToken = accessToken))
    }
    yield ()
  }
}
