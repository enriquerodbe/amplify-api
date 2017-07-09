package com.amplify.api.domain.models

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

case class Queue(
    currentPlaylist: Option[Playlist] = None,
    currentTrack: Option[Track] = None,
    items: List[QueueItem] = Nil,
    position: List[QueueItem] = Nil) {

  def setCurrentPlaylist(playlist: Playlist): Try[Queue] = Success {
    copy(currentPlaylist = Some(playlist), currentTrack = playlist.tracks.headOption)
  }

  def addVenueTrack(track: Track): Try[Queue] = Success {
    copy(items = items :+ QueueItem(track, QueueItemType.Venue))
  }

  def removeVenueTracks(): Try[Queue] = Success(copy(items = removeVenueTracks(items)))

  def removeAllTracks(): Try[Queue] = Success(Queue(currentPlaylist))

  @tailrec
  private def removeVenueTracks(items: List[QueueItem]): List[QueueItem] = items match {
    case Nil ⇒ Nil
    case head :: tail ⇒ head.itemType match {
      case QueueItemType.Venue ⇒ removeVenueTracks(tail)
      case QueueItemType.User ⇒ items
    }
  }

  def trackFinished(): Try[Queue] = Success {
    position match {
      case Nil ⇒ this
      case _ :: tail ⇒ copy(position = tail)
    }
  }

  def addUserTrack(user: User, identifier: ContentProviderIdentifier): Try[Queue] = {
    findTrack(identifier).map(track ⇒ copy(items = addUserTrack(position, Nil, track)))
  }

  private def findTrack(identifier: ContentProviderIdentifier): Try[Track] = {
    currentPlaylist match {
      case Some(playlist) ⇒ playlist.getTrack(identifier)
      case _ ⇒ Failure(new Exception)
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

  def skipCurrentTrack(): Try[Queue] = Success(items match {
    case _ :: (newPosition @ upNext :: rest) ⇒
      copy(currentTrack = Some(upNext.track), items = rest, position = newPosition)
    case _ ⇒
      copy(currentTrack = None, items = Nil, position = Nil)
  })
}
