package com.amplify.api.daos.models

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Id, Name, Token, Uid}

case class VenueDb(
    id: Id = -1L,
    name: Name,
    uid: Uid,
    identifier: AuthProviderIdentifier,
    fcmToken: Option[Token])
