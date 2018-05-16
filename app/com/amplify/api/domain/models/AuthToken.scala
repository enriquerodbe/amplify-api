package com.amplify.api.domain.models

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.{Token, TokenType}

case class AuthToken[T <: TokenType](authProvider: AuthProviderType, token: Token[T]) {

  override def toString: String = s"$authProvider:$token"
}
