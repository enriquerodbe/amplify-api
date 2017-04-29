package com.amplify.api.exceptions

import com.amplify.api.domain.models.ContentProviderIdentifier

trait UserException

case class UserNotFound(identifier: ContentProviderIdentifier)
  extends BadRequestException(AppExceptionCode.UserNotFound,
    s"User with identifier $identifier not found")
    with UserException
