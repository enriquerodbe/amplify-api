package com.amplify.api.services

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.{AuthenticationStrategiesRegistry, UserData}
import com.amplify.api.utils.FutureUtils.OptionT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
    registry: AuthenticationStrategiesRegistry)(
    implicit ec: ExecutionContext) extends AuthenticationService {

  override def fetchUser(
      authProviderType: AuthProviderType,
      authToken: String): Future[UserData] = {
    val strategy = registry.getStrategy(authProviderType)
    for {
      userData ← strategy.fetchUser(authToken)
      result ← userData ?! UserAuthTokenNotFound(authToken)
    }
    yield result
  }
}
