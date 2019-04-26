package com.amplify.api.domain.models

case class Queue(
    allowedPlaylist: Option[Playlist],
    pastItems: List[QueueItem],
    currentItem: Option[QueueItem],
    futureItems: List[QueueItem]) {

  def allItems: List[QueueItem] = pastItems ++ currentItem.toList ++ futureItems

  private def removeVenueTracks(): Queue = {
    copy(futureItems = futureItems.takeWhile(_.itemType == QueueItemType.User))
  }

  def addVenueTrack(track: Track): Queue = {
    val newItem = QueueItem(track, QueueItemType.Venue)

    currentItem match {
      case None ⇒ copy(currentItem = Some(newItem))
      case _ ⇒ copy(futureItems = futureItems :+ newItem)
    }
  }

  def setAllowedPlaylist(playlist: Playlist): Queue = copy(allowedPlaylist = Some(playlist))

  def finishCurrentTrack(): Queue = {
    copy(
      pastItems = pastItems ++ currentItem.toList,
      currentItem = futureItems.headOption,
      futureItems = futureItems.drop(1)
    )
  }

  def addPlaylistTracks(playlist: Playlist): Queue = {
    playlist.tracks.foldLeft(removeVenueTracks())(_.addVenueTrack(_))
  }

  def addUserTrack(identifier: TrackIdentifier): Queue = {
    val result = findTrack(identifier).map { track ⇒
      val userItems = futureItems.takeWhile(_.itemType == QueueItemType.User)
      val newItem = QueueItem(track, QueueItemType.User)
      val venueItems = futureItems.dropWhile(_.itemType == QueueItemType.User)

      copy(futureItems = (userItems :+ newItem) ++ venueItems)
    }

    result.getOrElse(this)
  }

  private def findTrack(identifier: TrackIdentifier): Option[Track] = {
    allowedPlaylist match {
      case Some(playlist) ⇒ playlist.findTrack(identifier)
      case _ ⇒ None
    }
  }

  def skipCurrentTrack(): Queue = finishCurrentTrack()
}

object Queue {

  val empty: Queue =
    Queue(allowedPlaylist = None, pastItems = Nil, currentItem = None, futureItems = Nil)
}
