package com.amplify.api.exceptions

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.domain.models.primitives.Identifier

trait UserException

case class UserNotFound(authProviderType: AuthProviderType, identifier: Identifier[User])
  extends BadRequestException(AppExceptionCode.UserNotFound,
    s"User with identifier $identifier not found")
    with UserException
