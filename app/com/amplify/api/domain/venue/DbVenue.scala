package com.amplify.api.domain.venue

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Access, Refresh, Token, _}

case class DbVenue(
    id: Id = Id(0L),
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    refreshToken: Token[Refresh],
    accessToken: Token[Access])
