package com.amplify.api.daos.models

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Id, Name, Token, Uid}

case class DbVenue(
    id: Id = Id(0L),
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    refreshToken: Token,
    accessToken: Token)
