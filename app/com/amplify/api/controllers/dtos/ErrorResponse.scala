package com.amplify.api.controllers.dtos

import com.amplify.api.exceptions.AppExceptionCode.AppExceptionCode

case class ErrorResponse(code: AppExceptionCode, message: String)
