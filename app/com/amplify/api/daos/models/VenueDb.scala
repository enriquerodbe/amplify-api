package com.amplify.api.daos.models

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Id, Name, Uid}

case class VenueDb(id: Id = Id(-1L), name: Name, uid: Uid, identifier: AuthProviderIdentifier)
