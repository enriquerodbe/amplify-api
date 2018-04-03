package com.amplify.api.aggregates.queue

import akka.actor.Actor
import akka.persistence.PersistentActor
import com.amplify.api.aggregates.queue.Command._
import com.amplify.api.aggregates.queue.CommandProcessor._
import com.amplify.api.aggregates.queue.Event._
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.daos.DbioRunner
import com.amplify.api.domain.models.Queue
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.assistedinject.Assisted
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CommandProcessor @Inject()(
    db: DbioRunner,
    envConfig: EnvConfig,
    @Assisted venueUid: Uid)(
    implicit ec: ExecutionContext) extends PersistentActor {

  override val persistenceId: String = venueUid.value

  private var queue = Queue.empty

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def receiveCommand: Receive = {
    case HandleCommand(command) ⇒
      val events = createEvents(command)
      persistAll(events.toList) { event ⇒
        queue = process(queue, event)
        context.system.eventStream.publish(event)
        val unit = ()
        sender() ! unit
      }

    case RetrieveState ⇒ sender() ! queue

    case SetState(newQueue) ⇒ queue = newQueue
  }

  override def receiveRecover: Receive = {
    case event: Event ⇒ queue = process(queue, event)
  }

  private def createEvents(command: Command): Seq[Event] = command match {
    case SetCurrentPlaylist(_, playlist) ⇒
      val tracksEvents = playlist.tracks.map(VenueTrackAdded)
      VenueTracksRemoved +: tracksEvents :+ CurrentPlaylistSet(playlist)

    case SkipCurrentTrack(_) ⇒
      Seq(CurrentTrackSkipped)

    case AddTrack(_, user, trackIdentifier) ⇒
      Seq(UserTrackAdded(user, trackIdentifier))
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

object CommandProcessor {

  trait Factory {
    def apply(venueUid: Uid): Actor
  }

  sealed trait CommandProcessorProtocol

  case class HandleCommand(command: Command) extends CommandProcessorProtocol

  case object RetrieveState extends CommandProcessorProtocol

  case class SetState(queue: Queue) extends CommandProcessorProtocol
}
