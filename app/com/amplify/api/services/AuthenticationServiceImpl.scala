package com.amplify.api.services

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.services.external.AuthProviderRegistry
import com.amplify.api.services.models.UserData
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
    registry: AuthProviderRegistry)(
    implicit ec: ExecutionContext) extends AuthenticationService {

  override def fetchUser(implicit authToken: AuthToken): Future[UserData] = {
    registry.getStrategy(authToken.authProvider).fetchUser(authToken)
  }
}
