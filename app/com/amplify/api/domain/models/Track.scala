package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class Track(name: Name, identifier: TrackIdentifier, album: Album)
