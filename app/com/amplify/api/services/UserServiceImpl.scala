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
    db: DbioRunner,
    userDao: UserDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def get(identifier: ContentProviderIdentifier): Future[AuthenticatedUser] = {
    val user = db.run(userDao.retrieve(identifier)) ?! UserNotFound(identifier)
    user.map(userDbToAuthenticatedUser)
  }

  override def getOrCreate(userData: UserData): Future[AuthenticatedUser] = {
    val userDb = userDataToUserDb(userData)
    val createdUser = db.runTransactionally(userDao.retrieveOrCreate(userDb))
    createdUser.map(userDbToAuthenticatedUser)
  }
}
