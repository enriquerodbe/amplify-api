package com.amplify.api.shared.services.external.models

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.Name

case class UserData(identifier: AuthProviderIdentifier, name: Name)
