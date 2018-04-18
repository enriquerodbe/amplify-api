package com.amplify.api.daos.models

import com.amplify.api.domain.models.AuthProviderIdentifier
import com.amplify.api.domain.models.primitives.{Id, Name}

case class DbUser(id: Id = Id(0L), name: Name, authIdentifier: AuthProviderIdentifier)
