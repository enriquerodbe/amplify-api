package com.amplify.api.services.external.models

import com.amplify.api.domain.models.ContentProviderIdentifier
import com.amplify.api.domain.models.primitives.Name

case class TrackData(identifier: ContentProviderIdentifier, name: Name, album: AlbumData)
