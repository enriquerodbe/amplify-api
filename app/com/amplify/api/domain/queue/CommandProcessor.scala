package com.amplify.api.domain.queue

import akka.actor.{Actor, Stash}
import com.amplify.api.domain.models.Queue
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.playlist.PlaylistService
import com.amplify.api.domain.queue.Command._
import com.amplify.api.domain.queue.CommandProcessor._
import com.amplify.api.shared.daos.DbioRunner
import com.google.inject.assistedinject.Assisted
import javax.inject.Inject
import play.api.Logger
import play.api.libs.concurrent.InjectedActorSupport
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private class CommandProcessor @Inject()(
    db: DbioRunner,
    playlistService: PlaylistService,
    queueEventDao: QueueEventDao,
    @Assisted venueUid: Uid)(
    implicit ec: ExecutionContext) extends Actor with Stash with InjectedActorSupport {

  private lazy val logger = Logger(classOf[CommandProcessor])
  private lazy val eventStream = context.system.eventStream

  private var queue = Queue.empty

  override def receive: Receive = {
    case HandleCommand(command) ⇒
      val commandSender = sender()
      val event = createEvent(command)
      val result = db.run(queueEventDao.create(event)).flatMap(_ ⇒ newState(queue, event))
      result.onComplete {
        case Failure(ex) ⇒
          logger.error(s"Processing command $command and event $event", ex)
          self ! FinishedProcessing(queue)
        case Success(newQueue) ⇒
          self ! FinishedProcessing(newQueue)
          eventStream.publish(QueueUpdated(event, newQueue))
          commandSender.!(())
      }
      context.become(processing)

    case RetrieveState ⇒ sender() ! queue

    case SetState(newQueue) ⇒
      queue = newQueue
      sender().!(())
  }

  private def processing: Receive = {
    case FinishedProcessing(newQueue) ⇒
      queue = newQueue
      unstashAll()
      context.become(receive)

    case SetState(newQueue) ⇒
      queue = newQueue
      sender().!(())

    case _ ⇒ stash()
  }

  private def createEvent(command: Command): QueueEvent = command match {
    case SetAllowedPlaylist(_, playlist) ⇒ AllowedPlaylistSet(venueUid, playlist)

    case StartPlayback(_) ⇒ PlaybackStarted(venueUid)

    case SkipCurrentTrack(_) ⇒ CurrentTrackSkipped(venueUid)

    case FinishCurrentTrack(_) ⇒ TrackFinished(venueUid)

    case AddPlaylistTracks(_, playlistIdentifier) ⇒
      PlaylistTracksAdded(venueUid, playlistIdentifier)

    case AddVenueTrack(_, trackIdentifier) ⇒ VenueTrackAdded(venueUid, trackIdentifier)

    case AddTrack(_, code, trackIdentifier) ⇒ UserTrackAdded(venueUid, code, trackIdentifier)
  }

  private def newState(queue: Queue, event: QueueEvent): Future[Queue] = event match {
    case AllowedPlaylistSet(uid, playlistIdentifier) ⇒
      playlistService.retrievePlaylist(uid, playlistIdentifier).map(queue.setAllowedPlaylist)

    case _: PlaybackStarted ⇒ Future.successful(queue)

    case _: CurrentTrackSkipped ⇒ Future.successful(queue.skipCurrentTrack())

    case _: TrackFinished ⇒ Future.successful(queue.finishCurrentTrack())

    case PlaylistTracksAdded(uid, playlistIdentifier) ⇒
      playlistService.retrievePlaylist(uid, playlistIdentifier).map(queue.addPlaylistTracks)

    case VenueTrackAdded(uid, trackIdentifier) ⇒
      playlistService.retrieveTrack(uid, trackIdentifier).map(queue.addVenueTrack)

    case UserTrackAdded(_, _, trackIdentifier) ⇒
      Future.successful(queue.addUserTrack(trackIdentifier))
  }

  override def preStart(): Unit = {
    db.run(queueEventDao.retrieve(venueUid)).map { events ⇒
      val initial = Future.successful(queue)
      val computation = events.foldLeft(initial)((state, event) ⇒ state.flatMap(newState(_, event)))
      computation.onComplete {
        case Success(result) ⇒ self ! FinishedProcessing(result)
        case Failure(_) ⇒ self ! FinishedProcessing(queue)
      }
    }
    context.become(processing)
  }
}

object CommandProcessor {

  trait Factory {
    def apply(venueUid: Uid): Actor
  }

  sealed trait CommandProcessorProtocol

  case class HandleCommand(command: Command) extends CommandProcessorProtocol

  case object RetrieveState extends CommandProcessorProtocol

  private case class FinishedProcessing(queue: Queue) extends CommandProcessorProtocol

  // For testing only
  case class SetState(queue: Queue) extends CommandProcessorProtocol
}
