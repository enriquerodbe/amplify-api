package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class Venue(name: Name[Venue], currentPlaylist: Playlist)
