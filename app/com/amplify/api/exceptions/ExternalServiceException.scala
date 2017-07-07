package com.amplify.api.exceptions

sealed trait ExternalServiceException

case class UnexpectedResponse(override val message: String)
  extends InternalException(AppExceptionCode.UnexpectedExternalServiceResponse, message)
  with ExternalServiceException

case object ExternalResourceNotFound
  extends InternalException(
    AppExceptionCode.ExternalResourceNotFound,
    "External resource not found")
    with ExternalServiceException
