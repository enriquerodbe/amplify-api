package com.amplify.api.domain.models

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType

case class AuthToken(contentProvider: ContentProviderType, token: String) {

  override def toString: String = s"$contentProvider:$token"
}
