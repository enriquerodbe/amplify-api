package com.amplify.api.domain.models

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.Token

case class AuthToken(authProvider: AuthProviderType, token: Token) {

  override def toString: String = s"$authProvider:$token"
}
