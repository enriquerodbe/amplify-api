package com.amplify.api.services

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.exceptions.{AppException, AppExceptionCode}
import com.amplify.api.services.external.{AuthenticationStrategiesRegistry, UserData}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
    registry: AuthenticationStrategiesRegistry)(
    implicit ec: ExecutionContext) extends AuthenticationService {

  override def fetchUser(
      authProviderType: AuthProviderType,
      authToken: String): Future[UserData] = {
    val strategy = registry.getStrategy(authProviderType)
    for (identifier ← strategy.fetchUser(authToken))
    yield identifier
  }
}

case class UserAuthTokenNotFound(authToken: String)
  extends AppException(
    AppExceptionCode.UserAuthTokenNotFound,
    s"User authentication token not found: $authToken")
