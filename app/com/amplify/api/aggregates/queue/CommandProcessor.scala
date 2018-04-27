package com.amplify.api.aggregates.queue

import akka.actor.Actor
import akka.persistence.PersistentActor
import com.amplify.api.aggregates.queue.Command._
import com.amplify.api.aggregates.queue.CommandProcessor._
import com.amplify.api.aggregates.queue.Event._
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.daos.DbioRunner
import com.amplify.api.domain.models.{Queue, Venue}
import com.google.inject.assistedinject.Assisted
import javax.inject.Inject
import play.api.libs.concurrent.InjectedActorSupport
import scala.concurrent.ExecutionContext

class CommandProcessor @Inject()(
    db: DbioRunner,
    envConfig: EnvConfig,
    playbackNotifierFactory: PlaybackNotifier.Factory,
    @Assisted venue: Venue)(
    implicit ec: ExecutionContext) extends PersistentActor with InjectedActorSupport {

  override val persistenceId: String = s"queue-${venue.uid}"

  implicit val askTimeout = envConfig.defaultAskTimeout

  private var queue = Queue.empty

  injectedChild(playbackNotifierFactory(venue), s"${venue.uid}-playback-notifier")

  override def receiveCommand: Receive = {
    case HandleCommand(command) ⇒
      val events = createEvents(command)
      val last = events.lastOption
      persistAll(events.toList) { event ⇒
        if (last.contains(event)) {
          queue = events.foldLeft(queue)(process)
          context.system.eventStream.publish(QueueUpdated(venue, event, queue))
          sender().!(())
        }
      }

    case RetrieveState ⇒ sender() ! queue

    case SetState(newQueue) ⇒
      queue = newQueue
      sender().!(())
  }

  override def receiveRecover: Receive = {
    case event: Event ⇒ queue = process(queue, event)
  }

  private def createEvents(command: Command): Seq[Event] = command match {
    case SetCurrentPlaylist(_, playlist) ⇒
      val tracksEvents = playlist.tracks.map(VenueTrackAdded)
      VenueTracksRemoved +: tracksEvents :+ CurrentPlaylistSet(playlist)

    case StartPlayback(_) ⇒ Seq(PlaybackStarted)

    case SkipCurrentTrack(_) ⇒ Seq(CurrentTrackSkipped)

    case FinishCurrentTrack(_) ⇒ Seq(TrackFinished)

    case AddTrack(_, coin, trackIdentifier) ⇒ Seq(UserTrackAdded(coin, trackIdentifier))
  }

  private def process(queue: Queue, event: Event): Queue = event match {
    case VenueTracksRemoved ⇒ queue.removeVenueTracks()
    case VenueTrackAdded(track) ⇒ queue.addVenueTrack(track)
    case CurrentPlaylistSet(playlist) ⇒ queue.setCurrentPlaylist(playlist)
    case PlaybackStarted ⇒ queue
    case TrackFinished ⇒ queue.finishCurrentTrack()
    case UserTrackAdded(_, identifier) ⇒ queue.addUserTrack(identifier)
    case CurrentTrackSkipped ⇒ queue.skipCurrentTrack()
  }
}

object CommandProcessor {

  trait Factory {
    def apply(venue: Venue): Actor
  }

  sealed trait CommandProcessorProtocol

  case class HandleCommand(command: Command) extends CommandProcessorProtocol

  case object RetrieveState extends CommandProcessorProtocol

  case class SetState(queue: Queue) extends CommandProcessorProtocol
}
