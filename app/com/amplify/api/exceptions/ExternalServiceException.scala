package com.amplify.api.exceptions

sealed trait ExternalServiceException

case class UnexpectedResponseException(message: String)
  extends InternalException(AppExceptionCode.UnexpectedExternalServiceResponse, message)
