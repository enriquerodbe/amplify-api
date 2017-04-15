package com.amplify.api.exceptions

case class UnsupportedAuthProvider(authProviderName: String)
  extends BadRequestException(
    AppExceptionCode.UnsupportedAuthProvider,
    s"Unsupported authentication provider: $authProviderName")
