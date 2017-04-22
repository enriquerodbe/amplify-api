package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Identifier

case class ContentProviderIdentifier[T](
    contentProvider: ContentProviderType,
    identifier: Identifier[T]) {

  override def toString: String = s"$contentProvider:$identifier"
}
