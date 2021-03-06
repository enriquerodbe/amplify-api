package com.amplify.api.domain.models

case class QueueItem(track: Track, itemType: QueueItemType) {

  def isUserTrack: Boolean = itemType == QueueItemType.User
}
