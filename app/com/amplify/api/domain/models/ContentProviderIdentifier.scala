package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Identifier
import com.amplify.api.exceptions.InvalidProviderIdentifier
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

case class ContentProviderIdentifier(
    contentProvider: ContentProviderType,
    identifier: Identifier) {

  override def toString: String = {
    s"$contentProvider${ContentProviderIdentifier.SEPARATOR}$identifier"
  }
}

object ContentProviderIdentifier {

  val SEPARATOR = ":"
  val regex = s"(\\w+)$SEPARATOR(.+)".r

  def fromString(identifier: String): Try[ContentProviderIdentifier] = {
    identifier match {
      case regex(provider, providerIdentifier) ⇒
        ContentProviderType.find(provider) match {
          case Some(providerType) ⇒ Success(providerType → providerIdentifier)
          case _ ⇒ Failure(InvalidProviderIdentifier(identifier))
        }
      case _ ⇒ Failure(InvalidProviderIdentifier(identifier))
    }
  }

  implicit def toString(id: ContentProviderIdentifier): String = id.toString

  implicit def fromTuple(tuple: (ContentProviderType, String)): ContentProviderIdentifier = {
    apply(tuple._1, tuple._2)
  }
}
