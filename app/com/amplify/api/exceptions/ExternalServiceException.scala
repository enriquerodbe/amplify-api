package com.amplify.api.exceptions

sealed trait ExternalServiceException

case class UnexpectedResponseException(override val message: String)
  extends InternalException(AppExceptionCode.UnexpectedExternalServiceResponse, message)
