package com.amplify.api.domain.logic

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserAuthLogicImpl])
trait UserAuthLogic {

  def login(authProviderType: AuthProviderType, authToken: String): Future[User]
}
