package com.amplify.api.services

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {

  def fetchUser(contentProvider: ContentProviderType, authToken: String): Future[UserData]
}
