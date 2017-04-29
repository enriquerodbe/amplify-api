package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthToken, AuthenticatedUser}
import com.amplify.api.services.{AuthenticationService, UserService}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAuthLogicImpl @Inject()(
    authService: AuthenticationService,
    userService: UserService)(
    implicit ec: ExecutionContext) extends UserAuthLogic {

  override def signUp(authToken: AuthToken): Future[Unit] = {
    for {
      userData ← authService.fetchUser(authToken)
      result ← userService.create(userData)
    }
    yield result
  }

  override def login(authToken: AuthToken): Future[AuthenticatedUser] = {
    for {
      userData ← authService.fetchUser(authToken)
      user ← userService.get(userData.identifier)
    }
    yield user
  }
}
