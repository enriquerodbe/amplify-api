package com.amplify.api.domain.models

case class Queue(
    currentPlaylist: Option[Playlist],
    pastItems: List[QueueItem],
    currentItem: Option[QueueItem],
    futureItems: List[QueueItem]) {

  def allItems: List[QueueItem] = pastItems ++ currentItem.toList ++ futureItems

  def removeVenueTracks(): Queue = {
    copy(futureItems = futureItems.takeWhile(_.itemType == QueueItemType.User))
  }

  def addVenueTrack(track: Track): Queue = {
    val newItem = QueueItem(track, QueueItemType.Venue)

    currentItem match {
      case None ⇒ copy(currentItem = Some(newItem))
      case _ ⇒ copy(futureItems = futureItems :+ newItem)
    }
  }

  def setCurrentPlaylist(playlist: Playlist): Queue = copy(currentPlaylist = Some(playlist))

  def finishCurrentTrack(): Queue = {
    copy(
      pastItems = pastItems ++ currentItem.toList,
      currentItem = futureItems.headOption,
      futureItems = futureItems.drop(1)
    )
  }

  def addUserTrack(identifier: ContentProviderIdentifier): Queue = {
    val result = findTrack(identifier).map { track ⇒
      val userItems = futureItems.takeWhile(_.itemType == QueueItemType.User)
      val newItem = QueueItem(track, QueueItemType.User)
      val venueItems = futureItems.dropWhile(_.itemType == QueueItemType.User)

      copy(futureItems = (userItems :+ newItem) ++ venueItems)
    }

    result.getOrElse(this)
  }

  def findTrack(identifier: ContentProviderIdentifier): Option[Track] = {
    currentPlaylist match {
      case Some(playlist) ⇒ playlist.findTrack(identifier)
      case _ ⇒ None
    }
  }

  def skipCurrentTrack(): Queue = finishCurrentTrack()
}

object Queue {

  def apply(): Queue = {
    Queue(currentPlaylist = None, pastItems = Nil, currentItem = None, futureItems = Nil)
  }
}
