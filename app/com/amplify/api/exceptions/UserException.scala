package com.amplify.api.exceptions

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{ContentProviderIdentifier, User}

trait UserException

case class UserNotFoundByIdentifier(identifier: ContentProviderIdentifier)
  extends UnauthorizedException(AppExceptionCode.UserNotFound,
    s"User with identifier $identifier not found")
    with UserException

case class UserNotFoundById(id: Id[User])
  extends UnauthorizedException(AppExceptionCode.UserNotFound,
    s"User with id $id not found")
    with UserException
