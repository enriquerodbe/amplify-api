package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.daos.schema.VenuesTable
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.{User, Venue}
import com.amplify.api.domain.models.primitives.{Identifier, Name}
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class VenueDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider) extends VenueDao with VenuesTable {

  import profile.api._

  override def create(user: UserDb, name: Name[Venue]): DBIO[VenueDb] = {
    (venuesTable returning venuesTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) +=
      VenueDb(name = name, userId = user.id)
  }

  override def retrieveAll: DBIO[Seq[VenueDb]] = venuesTable.result

  override def retrieve(
      identifier: Identifier[User],
      authProviderType: AuthProviderType): DBIO[Option[VenueDb]] = {
    val query =
      for {
        venue ← venuesTable
        user ← venue.user
        if user.authIdentifier === identifier && user.authProviderType === authProviderType
      }
      yield venue

    query.result.headOption
  }
}
