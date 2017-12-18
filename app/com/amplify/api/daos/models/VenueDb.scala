package com.amplify.api.daos.models

import com.amplify.api.domain.models.primitives.{Id, Name, Token, Uid}

case class VenueDb(id: Id = -1L, name: Name, userId: Id, uid: Uid, fcmToken: Option[Token])
