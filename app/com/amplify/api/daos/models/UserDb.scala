package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.AuthProviderType.AuthProviderType
import com.amplify.api.domain.models.User
import com.amplify.api.domain.models.primitives.{Email, Identifier, Name}

case class UserDb(
    id: Id[User] = -1L,
    name: Name[User],
    email: Email,
    authIdentifier: Identifier[User],
    authProviderType: AuthProviderType)
