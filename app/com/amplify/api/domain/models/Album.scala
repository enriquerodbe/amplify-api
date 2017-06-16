package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class Album(name: Name, artists: Seq[Artist], images: Seq[Image])
