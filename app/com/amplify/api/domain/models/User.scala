package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.{Email, Name}

case class User(name: Name[User], email: Email)
