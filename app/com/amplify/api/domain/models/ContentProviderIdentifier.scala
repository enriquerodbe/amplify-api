package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Identifier
import scala.language.implicitConversions

case class ContentProviderIdentifier(
    contentProvider: ContentProviderType,
    identifier: Identifier) {

  override def toString: String = s"$contentProvider:$identifier"
}

object ContentProviderIdentifier {

  implicit def toString(id: ContentProviderIdentifier): String = id.toString
}
