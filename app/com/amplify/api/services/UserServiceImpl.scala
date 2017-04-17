package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao}
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.exceptions.UserNotFound
import com.amplify.api.services.converters.UserConverter.userDbToUser
import com.amplify.api.services.external.UserData
import com.amplify.api.utils.FutureUtils.FutureT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(
    db: DbioRunner, userDao: UserDao)(
    implicit ec: ExecutionContext) extends UserService {

  override def get(
      userData: UserData,
      authProviderType: AuthProviderType): Future[User] = {
    val action = userDao.retrieve(userData.identifier, authProviderType).map(_.map(userDbToUser))
    db.run(action) ?! UserNotFound(authProviderType, userData.identifier)
  }
}