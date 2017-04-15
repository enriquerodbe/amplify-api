package com.amplify.api.exceptions

case class UnsupportedAuthProvider(authProviderName: String)
  extends AppException(
    AppExceptionCode.UnsupportedAuthProvider,
    s"Unsupported authentication provider: $authProviderName")
