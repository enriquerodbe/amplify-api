package com.amplify.api.domain.models

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType

case class AuthToken(authProvider: AuthProviderType, token: String) {

  override def toString: String = s"$authProvider:$token"
}
