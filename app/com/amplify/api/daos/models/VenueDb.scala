package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{User, Venue}
import com.amplify.api.domain.models.primitives.Name

case class VenueDb(id: Id[Venue] = -1L, name: Name[Venue], userId: Id[User])
