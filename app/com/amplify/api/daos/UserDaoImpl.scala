package com.amplify.api.daos

import com.amplify.api.daos.models.UserDb
import com.amplify.api.daos.schema.UsersTable
import com.amplify.api.domain.models.AuthProviderIdentifier
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class UserDaoImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends UserDao with UsersTable {

  import profile.api._

  override def retrieve(identifier: AuthProviderIdentifier): DBIO[Option[UserDb]] = {
    val query = usersTable.filter { user ⇒
      user.authIdentifier === identifier.identifier &&
        user.authProviderType === identifier.authProvider
    }

    query.result.headOption
  }
}
