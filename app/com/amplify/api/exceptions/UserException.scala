package com.amplify.api.exceptions

import com.amplify.api.domain.models.ContentProviderIdentifier

trait UserException

case class UserNotFound(identifier: ContentProviderIdentifier)
  extends ForbiddenException(AppExceptionCode.UserNotFound,
    s"User with identifier $identifier not found")
    with UserException
