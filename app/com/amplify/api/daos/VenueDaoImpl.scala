package com.amplify.api.daos

import com.amplify.api.daos.models.VenueDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.{UsersTable, VenuesTable}
import com.amplify.api.domain.models.{ContentProviderIdentifier, User, Venue}
import com.amplify.api.exceptions.VenueNotFound
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext)
  extends VenueDao with VenuesTable with UsersTable {

  import profile.api._

  override def create(venueDb: VenueDb): DBIO[VenueDb] = {
    (venuesTable returning venuesTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) += venueDb
  }

  override def retrieve(userId: Id[User]): DBIO[Option[VenueDb]] = {
    venuesTable.filter(_.userId === userId).take(1).result.headOption
  }

  override def retrieve(userIdentifier: ContentProviderIdentifier): DBIO[VenueDb] = {
    val query =
      for {
        venue ← venuesTable
        user ← usersTable
        if venue.userId === user.id &&
          user.authProviderType === userIdentifier.contentProvider &&
          user.authIdentifier === userIdentifier.identifier
      }
      yield venue

    query.result.headOption.flatMap {
      case Some(venue) ⇒ DBIO.successful(venue)
      case _ ⇒ DBIO.failed(VenueNotFound(userIdentifier))
    }
  }

  override def updateCurrentPlaylist(
      id: Id[Venue],
      playlistIdentifier: ContentProviderIdentifier): DBIO[Unit] = {
    val query = venuesTable.filter(_.id === id).map(_.currentPlaylistProviderIdentifier)
    query.update(Some(playlistIdentifier)).map(_ ⇒ ())
  }
}
