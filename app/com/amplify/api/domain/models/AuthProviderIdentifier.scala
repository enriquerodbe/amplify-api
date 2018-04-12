package com.amplify.api.domain.models

import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.exceptions.InvalidProviderIdentifier
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

case class AuthProviderIdentifier(authProvider: AuthProviderType, identifier: Identifier) {

  override def toString: String = {
    s"$authProvider${AuthProviderIdentifier.SEPARATOR}$identifier"
  }
}

object AuthProviderIdentifier {

  val SEPARATOR = ":"
  val regex = s"(\\w+)$SEPARATOR(.+)".r

  def fromString(identifier: String): Try[AuthProviderIdentifier] = {
    identifier match {
      case regex(provider, providerIdentifier) ⇒
        AuthProviderType.find(provider) match {
          case Some(providerType) ⇒ Success(providerType → providerIdentifier)
          case _ ⇒ Failure(InvalidProviderIdentifier(identifier))
        }
      case _ ⇒ Failure(InvalidProviderIdentifier(identifier))
    }
  }

  implicit def fromTuple(tuple: (AuthProviderType, String)): AuthProviderIdentifier = {
    apply(tuple._1, Identifier(tuple._2))
  }
}
