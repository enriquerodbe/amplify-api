package com.amplify.api.aggregates.queue.daos

import com.amplify.api.domain.models.primitives.PrimitivesSerializer._
import com.amplify.api.domain.models.{AuthProviderIdentifier, ContentProviderIdentifier}
import play.api.libs.json.{Reads, __}

object CommonReads {

  val userIdentifierField = "user_identifier"
  val trackIdentifierField = "track_identifier"
  val playlistIdentifierField = "playlist_identifier"

  val userIdentifierReads: Reads[AuthProviderIdentifier] =
    (__ \ userIdentifierField).read[AuthProviderIdentifier]
  val trackIdentifierReads: Reads[ContentProviderIdentifier] =
    (__ \ trackIdentifierField).read[ContentProviderIdentifier]

  val playlistIdentifierReads: Reads[ContentProviderIdentifier] =
    (__ \ playlistIdentifierField).read[ContentProviderIdentifier]
}
