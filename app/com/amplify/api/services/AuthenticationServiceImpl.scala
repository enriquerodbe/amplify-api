package com.amplify.api.services

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.exceptions.UserAuthTokenNotFound
import com.amplify.api.services.external.{ContentProviderRegistry, UserData}
import com.amplify.api.utils.FutureUtils.OptionT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
    registry: ContentProviderRegistry)(
    implicit ec: ExecutionContext) extends AuthenticationService {

  override def fetchUser(
      contentProviderType: ContentProviderType,
      authToken: String): Future[UserData] = {
    val strategy = registry.getStrategy(contentProviderType)
    for {
      userData ← strategy.fetchUser(authToken)
      result ← userData ?! UserAuthTokenNotFound(authToken)
    }
    yield result
  }
}
