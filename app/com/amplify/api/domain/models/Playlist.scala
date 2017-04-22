package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Name

case class Playlist(name: Name[Playlist], identifier: ContentProviderIdentifier[Playlist])
