package com.amplify.api.domain.venue

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Access, Refresh, Token, _}

case class DbVenue(
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    refreshToken: Token[Refresh],
    accessToken: Token[Access])
