package com.amplify.api.services.external.models

import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.domain.models.primitives.{Email, Name}

case class UserData(identifier: ContentProviderIdentifier, name: Name, email: Email)
