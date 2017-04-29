package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.Identifier

case class SongEvent(eventType: SongEventType, song: Song, relatedEventId: SongEvent)

sealed trait SongEventType
case object PlaySongRequested extends SongEventType
case object PlaySongAccepted extends SongEventType
case object PlaySongRejected extends SongEventType
case object PlaySongCanceled extends SongEventType
case object SongStartedPlaying extends SongEventType
case object SongSkipped extends SongEventType
case object SongFinishedPlaying extends SongEventType

case class Song(source: SongSource, identifier: Identifier)

sealed trait SongSource {

  case object SpotifyUri extends SongSource
}
