package com.amplify.api.aggregates.queue

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import com.amplify.api.aggregates.queue.CommandConverter.commandToCommandDb
import com.amplify.api.aggregates.queue.CommandProcessor._
import com.amplify.api.aggregates.queue.CommandType.QueueCommandType
import com.amplify.api.aggregates.queue.Event._
import com.amplify.api.aggregates.queue.EventConverter.queueEventToQueueEventDb
import com.amplify.api.aggregates.queue.MaterializedView.{EventsBatch, Materialize, SetState}
import com.amplify.api.aggregates.queue.daos.{CommandDao, EventDao}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Id
import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class CommandProcessor @Inject()(
    envConfig: EnvConfig,
    @Named("push-notification-gateway") pushNotificationGateway: ActorRef,
    commandDao: CommandDao,
    eventDao: EventDao)(
    implicit ec: ExecutionContext) extends Actor {

  val materializedView = context.actorOf(Props[MaterializedView])

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def receive: Receive = {
    case command: Command ⇒
      val origSender = sender()
      val future =
        for {
          commandDb ← commandDao.create(commandToCommandDb(command))
          events = createEvents(command)
          _ ← eventDao.create(events.map(queueEventToQueueEventDb(commandDb, _)))
        }
        yield {
          materializedView ! EventsBatch(events)
          pushNotificationGateway ! command
        }
      future pipeTo origSender

    case RetrieveMaterialized ⇒
      materializedView forward Materialize

    case RetrieveCurrentPlaylist ⇒
      val origSender = sender()
      val eventualQueue = (materializedView ? Materialize).mapTo[Queue]
      eventualQueue.map(_.currentPlaylist) pipeTo origSender

    case setState: SetState ⇒
      materializedView forward setState
  }

  private def createEvents(command: Command): Seq[Event] = command match {
    case SetCurrentPlaylist(_, playlist) ⇒
      val tracksEvents = playlist.tracks.map(VenueTrackAdded)
      VenueTracksRemoved +: tracksEvents :+ CurrentPlaylistSet(playlist)

    case SkipCurrentTrack(_) ⇒
      Seq(CurrentTrackSkipped)

    case StartPlayback(_) | PausePlayback(_) ⇒
      Seq.empty[Event]

    case AddTrack(_, user, trackIdentifier) ⇒
      Seq(UserTrackAdded(user, trackIdentifier))
  }
}

object CommandProcessor {

  trait Factory {
    def apply(): Actor
  }

  case object RetrieveCurrentPlaylist

  case object RetrieveMaterialized

  sealed trait Command {

    def venue: UnauthenticatedVenue

    def userId: Option[Id] = None

    def queueCommandType: QueueCommandType

    def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class SetCurrentPlaylist(venue: UnauthenticatedVenue, playlist: Playlist) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.SetCurrentPlaylist

    override def contentIdentifier: Option[ContentProviderIdentifier] = {
      Some(playlist.info.identifier)
    }
  }

  case class StartPlayback(venue: UnauthenticatedVenue) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.StartPlayback
  }

  case class PausePlayback(venue: UnauthenticatedVenue) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.PausePlayback
  }

  case class SkipCurrentTrack(venue: UnauthenticatedVenue) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.SkipCurrentTrack
  }

  case class AddTrack(
      venue: UnauthenticatedVenue,
      user: User,
      trackIdentifier: ContentProviderIdentifier) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.AddTrack
  }
}
