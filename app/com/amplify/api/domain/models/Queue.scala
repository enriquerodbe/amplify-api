package com.amplify.api.domain.models

import scala.annotation.tailrec

case class Queue(items: List[QueueItem] = Nil) {

  def addVenueTrack(track: Track): Queue = Queue(QueueItem(track, QueueItemType.Venue) :: items)

  def removeVenueTracks(): Queue = Queue(removeVenueTracks(items))

  def startPlaying(): Queue = Queue(items)

  @tailrec
  private def removeVenueTracks(items: List[QueueItem]): List[QueueItem] = items match {
    case Nil ⇒ Nil
    case head :: tail ⇒ head.itemType match {
      case QueueItemType.Venue ⇒ removeVenueTracks(tail)
      case QueueItemType.User ⇒ items
    }
  }
}
