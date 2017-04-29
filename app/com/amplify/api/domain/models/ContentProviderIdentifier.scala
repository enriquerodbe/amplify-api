package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Identifier

case class ContentProviderIdentifier(
    contentProvider: ContentProviderType,
    identifier: Identifier) {

  override def toString: String = s"$contentProvider:$identifier"
}
