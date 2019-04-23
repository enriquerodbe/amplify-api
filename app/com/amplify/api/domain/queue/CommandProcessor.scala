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
import play.api.libs.concurrent.InjectedActorSupport
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private class CommandProcessor @Inject()(
    db: DbioRunner,
    playlistService: PlaylistService,
    queueEventDao: QueueEventDao,
    @Assisted venueUid: Uid)(
    implicit ec: ExecutionContext) extends Actor with Stash with InjectedActorSupport {

  private lazy val eventStream = context.system.eventStream

  private var queue = Queue.empty

  override def receive: Receive = {
    case HandleCommand(command) ⇒
      val commandSender = sender()
      val event = createEvent(command)
      val result = db.run(queueEventDao.create(event)).flatMap(_ ⇒ newState(queue, event))
      result.onComplete {
        case Failure(_) ⇒ self ! FinishedProcessing(queue)
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
    case SetCurrentPlaylist(_, playlist) ⇒ CurrentPlaylistSet(venueUid, playlist)

    case StartPlayback(_) ⇒ PlaybackStarted(venueUid)

    case SkipCurrentTrack(_) ⇒ CurrentTrackSkipped(venueUid)

    case FinishCurrentTrack(_) ⇒ TrackFinished(venueUid)

    case AddTrack(_, coin, trackIdentifier) ⇒ UserTrackAdded(venueUid, coin.code, trackIdentifier)
  }

  private def newState(queue: Queue, event: QueueEvent): Future[Queue] = event match {
    case CurrentPlaylistSet(uid, playlistIdentifier) ⇒
      playlistService.retrievePlaylist(uid, playlistIdentifier).map(queue.setCurrentPlaylist)

    case _: PlaybackStarted ⇒ Future.successful(queue)

    case _: TrackFinished ⇒ Future.successful(queue.finishCurrentTrack())

    case UserTrackAdded(_, _, trackIdentifier) ⇒
      Future.successful(queue.addUserTrack(trackIdentifier))

    case _: CurrentTrackSkipped ⇒ Future.successful(queue.skipCurrentTrack())
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
