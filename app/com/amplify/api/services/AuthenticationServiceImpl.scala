package com.amplify.api.services

import com.amplify.api.domain.models.AuthToken
import com.amplify.api.services.external.{ContentProviderRegistry, UserData}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
    registry: ContentProviderRegistry)(
    implicit ec: ExecutionContext) extends AuthenticationService {

  override def fetchUser(authToken: AuthToken): Future[UserData] = {
    registry.getStrategy(authToken.contentProvider).fetchUser(authToken.token)
  }
}
