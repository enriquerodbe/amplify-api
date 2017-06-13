package com.amplify.api.daos.models

import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{ContentProviderIdentifier, User}

case class UserDb(id: Id[User] = -1L, name: Name, authIdentifier: ContentProviderIdentifier)
