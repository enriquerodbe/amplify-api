package com.amplify.api.domain.models

import scala.annotation.tailrec

case class Queue(
    currentPlaylist: Option[Playlist] = None,
    currentTrack: Option[Track] = None,
    items: List[QueueItem] = Nil,
    position: List[QueueItem] = Nil) {

  def setCurrentPlaylist(playlist: Playlist): Queue = {
    copy(currentPlaylist = Some(playlist), currentTrack = playlist.tracks.headOption)
  }

  def addVenueTrack(track: Track): Queue = {
    copy(items = items :+ QueueItem(track, QueueItemType.Venue))
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

  def trackFinished(): Queue = {
    position match {
      case Nil ⇒ this
      case _ :: tail ⇒ copy(position = tail)
    }
  }

  def addUserTrack(user: User, identifier: ContentProviderIdentifier): Queue = {
    findTrack(identifier).map(track ⇒ copy(items = addUserTrack(position, Nil, track)))
      .getOrElse(this)
  }

  private def findTrack(identifier: ContentProviderIdentifier): Option[Track] = {
    currentPlaylist match {
      case Some(playlist) ⇒ playlist.findTrack(identifier)
      case _ ⇒ None
    }
  }

  @tailrec
  private def addUserTrack(
      items: List[QueueItem],
      acc: List[QueueItem],
      track: Track): List[QueueItem] = items match {
    case Nil ⇒ acc :+ QueueItem(track, QueueItemType.User)
    case head :: tail ⇒
      head.itemType match {
        case QueueItemType.User ⇒ addUserTrack(tail, acc :+ head, track)
        case QueueItemType.Venue ⇒ (acc :+ QueueItem(track, QueueItemType.User)) ++ items
      }
  }

  def skipCurrentTrack(): Queue = items match {
    case _ :: (newPosition @ upNext :: _) ⇒
      copy(currentTrack = Some(upNext.track), position = newPosition)
    case _ ⇒
      copy(currentTrack = None, items = Nil, position = Nil)
  }
}
