package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.services.{AuthenticationService, UserService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    userService: UserService)(
    implicit ec: ExecutionContext) extends UserAuthLogic {

  override def login(authProviderType: AuthProviderType, authToken: String): Future[User] = {
    for {
      userData ← authService.fetchUser(authProviderType, authToken)
      user ← userService.get(userData, authProviderType)
    }
    yield user
  }
}
