package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.Event._
import com.amplify.api.aggregates.queue.MaterializedView.{EventsBatch, Materialize, SetState}
import com.amplify.api.domain.models._

class MaterializedView extends Actor {

  private var queue = Queue()

  override def receive: Receive = {
    case EventsBatch(events) ⇒ queue = events.foldLeft(queue)(process)

    case Materialize ⇒ sender() ! queue

    case SetState(newQueue) ⇒ queue = newQueue
  }

  private def process(queue: Queue, event: Event): Queue = event match {
    case VenueTracksRemoved ⇒ removeVenueTracks(queue)
    case VenueTrackAdded(track) ⇒ addVenueTrack(queue, track)
    case CurrentPlaylistSet(playlist) ⇒ setCurrentPlaylist(queue, playlist)
    case TrackFinished ⇒ finishCurrentTrack(queue)
    case UserTrackAdded(_, identifier) ⇒ addUserTrack(queue, identifier)
    case CurrentTrackSkipped ⇒ skipCurrentTrack(queue)
  }

  private def removeVenueTracks(queue: Queue): Queue = {
    queue.copy(futureItems = queue.futureItems.takeWhile(_.itemType == QueueItemType.User))
  }

  private def addVenueTrack(queue: Queue, track: Track): Queue = {
    queue.copy(futureItems = queue.futureItems :+ QueueItem(track, QueueItemType.Venue))
  }

  private def setCurrentPlaylist(queue: Queue, playlist: Playlist): Queue = {
    queue.copy(currentPlaylist = Some(playlist))
  }

  private def finishCurrentTrack(queue: Queue): Queue = {
    queue.copy(
      pastItems = queue.pastItems ++ queue.currentItem.toList,
      currentItem = queue.futureItems.headOption,
      futureItems = queue.futureItems.drop(1)
    )
  }

  private def addUserTrack(queue: Queue, identifier: ContentProviderIdentifier): Queue = {
    val result = findTrack(identifier).map { item ⇒
      val userItems = queue.futureItems.takeWhile(_.itemType == QueueItemType.User)
      val newItem = QueueItem(item, QueueItemType.User)
      val venueItems = queue.futureItems.dropWhile(_.itemType == QueueItemType.User)

      queue.copy(futureItems = (userItems :+ newItem) ++ venueItems)
    }

    result.getOrElse(queue)
  }

  private def findTrack(identifier: ContentProviderIdentifier): Option[Track] = {
    queue.currentPlaylist match {
      case Some(playlist) ⇒ playlist.findTrack(identifier)
      case _ ⇒ None
    }
  }

  private def skipCurrentTrack(queue: Queue): Queue = finishCurrentTrack(queue)
}

object MaterializedView {

  case class SetState(queue: Queue)

  case object Materialize

  case class EventsBatch(events: Seq[Event])
}
