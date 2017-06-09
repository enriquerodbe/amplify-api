package com.amplify.api.domain.models

import scala.annotation.tailrec

case class Queue(
    currentPlaylist: Option[ContentProviderIdentifier] = None,
    items: List[QueueItem] = Nil,
    position: List[QueueItem] = Nil) {

  def setCurrentPlaylist(contentIdentifier: ContentProviderIdentifier): Queue = {
    copy(currentPlaylist = Some(contentIdentifier))
  }


  def addVenueTrack(track: Track): Queue = {
    copy(items = QueueItem(track, QueueItemType.Venue) :: items)
  }

  def removeVenueTracks(): Queue = copy(items = removeVenueTracks(items))

  def removeAllTracks(): Queue = Queue(currentPlaylist)

  @tailrec
  private def removeVenueTracks(items: List[QueueItem]): List[QueueItem] = items match {
    case Nil ⇒ Nil
    case head :: tail ⇒ head.itemType match {
      case QueueItemType.Venue ⇒ removeVenueTracks(tail)
      case QueueItemType.User ⇒ items
    }
  }

  def trackFinished(): Queue = position match {
    case Nil ⇒ this
    case _ :: tail ⇒ Queue(currentPlaylist, items, tail)
  }
}
