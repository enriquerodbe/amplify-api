package com.amplify.api.aggregates.queue

import akka.actor.{Actor, Props}
import akka.pattern.{ask, pipe}
import com.amplify.api.aggregates.queue.CommandConverter.commandToCommandDb
import com.amplify.api.aggregates.queue.CommandProcessor._
import com.amplify.api.aggregates.queue.CommandType.QueueCommandType
import com.amplify.api.aggregates.queue.Event.{CurrentPlaylistSet, CurrentTrackSkipped, VenueTrackAdded, VenueTracksRemoved}
import com.amplify.api.aggregates.queue.EventConverter.queueEventToQueueEventDb
import com.amplify.api.aggregates.queue.MaterializedView.{EventsBatch, Materialize, SetState}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Id
import com.amplify.api.services.VenueService
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CommandProcessor @Inject()(
    envConfig: EnvConfig,
    commandDao: CommandDao,
    eventDao: EventDao,
    venueService: VenueService)(
    implicit ec: ExecutionContext) extends Actor {

  val materializedView = context.actorOf(Props[MaterializedView])

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def receive: Receive = {
    case command: Command ⇒
      val origSender = sender()
      val future =
        for {
          commandDb ← commandDao.create(commandToCommandDb(command))
          events ← createEvents(command)
          _ ← eventDao.create(events.map(queueEventToQueueEventDb(commandDb, _)))
        }
        yield materializedView ! EventsBatch(events)
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

  private def createEvents(command: Command): Future[Seq[Event]] = command match {
    case SetCurrentPlaylist(venue, playlistIdentifier) ⇒
      val eventualPlaylist = venueService.retrievePlaylist(venue, playlistIdentifier)
      eventualPlaylist.map { playlist ⇒
        val tracksEvents = playlist.tracks.map(VenueTrackAdded)
        VenueTracksRemoved +: tracksEvents :+ CurrentPlaylistSet(playlist)
      }

    case SkipCurrentTrack(_) ⇒
      Future.successful(Seq(CurrentTrackSkipped))

    case StartPlayback(_) | PausePlayback(_) ⇒
      Future.successful(Seq.empty[Event])
  }
}

object CommandProcessor {

  trait Factory {
    def apply(): Actor
  }

  case object RetrieveCurrentPlaylist

  case object RetrieveMaterialized

  sealed trait Command {

    def venue: AuthenticatedVenueReq

    def userId: Option[Id] = None

    def queueCommandType: QueueCommandType

    def contentIdentifier: Option[ContentProviderIdentifier] = None
  }

  case class SetCurrentPlaylist(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.SetCurrentPlaylist

    override def contentIdentifier: Option[ContentProviderIdentifier] = Some(playlistIdentifier)
  }

  case class StartPlayback(venue: AuthenticatedVenueReq) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.StartPlayback
  }

  case class PausePlayback(venue: AuthenticatedVenueReq) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.PausePlayback
  }

  case class SkipCurrentTrack(venue: AuthenticatedVenueReq) extends Command {

    override def queueCommandType: QueueCommandType = CommandType.SkipCurrentTrack
  }
}
