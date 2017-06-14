package com.amplify.api.daos

import com.amplify.api.daos.models.UserDb
import com.amplify.api.daos.primitives.Id
import com.amplify.api.daos.schema.UsersTable
import com.amplify.api.domain.models.{ContentProviderIdentifier, User}
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext

class UserDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext) extends UserDao with UsersTable {

  import profile.api._

  override def retrieve(id: Id[User]): DBIO[Option[UserDb]] = {
    usersTable.filter(_.id === id).result.headOption
  }

  override def retrieve(identifier: ContentProviderIdentifier): DBIO[Option[UserDb]] = {
    val query = usersTable.filter { user ⇒
      user.authIdentifier === identifier.identifier &&
        user.authProviderType === identifier.contentProvider
    }

    query.result.headOption
  }

  override def retrieveOrCreate(userDb: UserDb): DBIO[UserDb] = {
    val maybeExistingUser = retrieve(userDb.authIdentifier)
    maybeExistingUser.flatMap {
      case Some(user) ⇒ DBIO.successful(user)
      case _ ⇒ create(userDb)
    }
  }

  private def create(user: UserDb): DBIO[UserDb] = {
    (usersTable returning usersTable.map(_.id) into ((obj, id) ⇒ obj.copy(id = id))) += user
  }
}
