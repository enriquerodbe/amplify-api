package com.amplify.api.services

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.services.external.{ContentProviderRegistry, UserData}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticationServiceImpl @Inject()(
    registry: ContentProviderRegistry)(
    implicit ec: ExecutionContext) extends AuthenticationService {

  override def fetchUser(
      contentProviderType: ContentProviderType,
      authToken: String): Future[UserData] = {
    registry.getStrategy(contentProviderType).fetchUser(authToken)
  }
}
