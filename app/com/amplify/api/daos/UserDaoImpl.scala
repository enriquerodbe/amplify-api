package com.amplify.api.daos

import com.amplify.api.daos.models.{UserDb, VenueDb}
import com.amplify.api.daos.schema.{UsersTable, VenuesTable}
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.domain.models.primitives.Identifier
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class UserDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider) extends UserDao with UsersTable with VenuesTable {

  import profile.api._

  override def create(user: UserDb): DBIO[UserDb] = {
    (usersTable returning usersTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) += user
  }

  override def retrieve(
      identifier: Identifier[User],
      authProviderType: AuthProviderType): DBIO[Option[(UserDb, Option[VenueDb])]] = {
    val all = usersTable joinLeft venuesTable on (_.id === _.userId)

    val query = all.filter { case (user, _) ⇒
      user.authIdentifier === identifier && user.authProviderType === authProviderType
    }

    query.result.headOption
  }
}
