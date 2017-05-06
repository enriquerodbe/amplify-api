package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{ContentProviderIdentifier, User, Venue}

case class VenueDb(
    id: Id[Venue] = -1L,
    name: Name,
    userId: Id[User],
    currentPlaylist: Option[ContentProviderIdentifier])
