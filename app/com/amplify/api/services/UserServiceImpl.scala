package com.amplify.api.services

import com.amplify.api.daos.models.UserDb
import com.amplify.api.daos.{DbioRunner, UserDao}
import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.exceptions.UserNotFound
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.external.UserData
import com.amplify.api.utils.FutureUtils.FutureT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.DBIO

class UserServiceImpl @Inject()(
    db: DbioRunner,
    userDao: UserDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def get(identifier: ContentProviderIdentifier): Future[AuthenticatedUser] = {
    db.run(maybeGet(identifier).map(_.map(userDbToAuthenticatedUser))) ?! UserNotFound(identifier)
  }

  override def getOrCreate(userData: UserData): Future[AuthenticatedUser] = {
    db.runTransactionally(this.getOrCreateAction(userData).map(userDbToAuthenticatedUser))
  }

  private def maybeGet(identifier: ContentProviderIdentifier): DBIO[Option[UserDb]] = {
    userDao.retrieve(identifier)
  }

  private def create(userData: UserData): DBIO[UserDb] = {
    userDao.create(userDataToUserDb(userData))
  }

  override private[services] def getOrCreateAction(userData: UserData): DBIO[UserDb] = {
    val maybeExistingUser = this.maybeGet(userData.identifier)
    maybeExistingUser.flatMap {
      case Some(user) ⇒ DBIO.successful(user)
      case _ ⇒ this.create(userData)
    }
  }
}
