package com.amplify.api.domain.models

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.Identifier
import scala.language.implicitConversions

case class AuthProviderIdentifier(authProvider: AuthProviderType, identifier: Identifier) {

  override def toString: String = {
    s"$authProvider${ContentProviderIdentifier.SEPARATOR}$identifier"
  }
}

object AuthProviderIdentifier {

  implicit def toString(id: ContentProviderIdentifier): String = id.toString

  implicit def fromTuple(tuple: (AuthProviderType, String)): AuthProviderIdentifier = {
    apply(tuple._1, tuple._2)
  }
}
