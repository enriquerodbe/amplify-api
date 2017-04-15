package com.amplify.api.services

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {

  def fetchUser(authProvider: AuthProviderType, authToken: String): Future[UserData]
}
