package com.amplify.api.exceptions

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.Id

trait UserException

case class UserNotFoundByIdentifier(identifier: AuthProviderIdentifier)
  extends UnauthorizedException(AppExceptionCode.UserNotFound,
    s"User with identifier $identifier not found")
    with UserException

case class UserNotFoundById(id: Id)
  extends UnauthorizedException(AppExceptionCode.UserNotFound,
    s"User with id $id not found")
    with UserException
