package com.amplify.api.exceptions

import com.amplify.api.domain.models.{ContentProviderIdentifier, User}

trait UserException

case class UserNotFound(identifier: ContentProviderIdentifier[User])
  extends BadRequestException(AppExceptionCode.UserNotFound,
    s"User with identifier $identifier not found")
    with UserException
