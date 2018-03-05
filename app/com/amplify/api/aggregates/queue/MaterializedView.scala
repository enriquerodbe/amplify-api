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
    case VenueTracksRemoved ⇒ queue.removeVenueTracks()
    case VenueTrackAdded(track) ⇒ queue.addVenueTrack(track)
    case CurrentPlaylistSet(playlist) ⇒ queue.setCurrentPlaylist(playlist)
    case TrackFinished ⇒ queue.finishCurrentTrack()
    case UserTrackAdded(_, identifier) ⇒ queue.addUserTrack(identifier)
    case CurrentTrackSkipped ⇒ queue.skipCurrentTrack()
  }
}

object MaterializedView {

  sealed trait MaterializedViewProtocol

  case class SetState(queue: Queue) extends MaterializedViewProtocol

  case object Materialize extends MaterializedViewProtocol

  case class EventsBatch(events: Seq[Event]) extends MaterializedViewProtocol
}
