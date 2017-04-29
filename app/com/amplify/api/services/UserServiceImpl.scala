package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao}
import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.exceptions.UserNotFound
import com.amplify.api.services.converters.UserConverter.{userDataToUserDb, userDbToAuthenticatedUser}
import com.amplify.api.services.external.UserData
import com.amplify.api.utils.FutureUtils.FutureT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
    db: DbioRunner, userDao: UserDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def create(userData: UserData): Future[Unit] = {
    val action = userDao.create(userDataToUserDb(userData))
    db.runTransactionally(action).map(_ ⇒ ())
  }

  override def get(identifier: ContentProviderIdentifier): Future[AuthenticatedUser] = {
    val action = userDao.retrieve(identifier).map(_.map(userDbToAuthenticatedUser))
    db.run(action) ?! UserNotFound(identifier)
  }
}
