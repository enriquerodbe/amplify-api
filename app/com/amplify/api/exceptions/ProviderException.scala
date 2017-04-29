package com.amplify.api.exceptions

trait ProviderException

case class SpotifyException(override val message: String)
  extends InternalException(AppExceptionCode.SpotifyError, message) with ProviderException
