package com.amplify.api.daos.models

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Id, Name}

case class UserDb(id: Id = Id(-1L), name: Name, authIdentifier: AuthProviderIdentifier)
