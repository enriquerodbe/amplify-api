package com.amplify.api.aggregates.queue

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.pipe
import com.amplify.api.aggregates.queue.Command._
import com.amplify.api.aggregates.queue.CommandConverter.commandToCommandDb
import com.amplify.api.aggregates.queue.CommandProcessor._
import com.amplify.api.aggregates.queue.Event._
import com.amplify.api.aggregates.queue.EventConverter.queueEventToQueueEventDb
import com.amplify.api.aggregates.queue.MaterializedView.{EventsBatch, Materialize, SetState}
import com.amplify.api.aggregates.queue.PushNotificationGateway.NotifyEvents
import com.amplify.api.aggregates.queue.daos.{CommandDao, EventDao}
import com.amplify.api.configuration.EnvConfig
import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.exceptions.VenueNotFoundByUid
import com.amplify.api.utils.DbioUtils.DbioT
import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext

class CommandProcessor @Inject()(
    db: DbioRunner,
    envConfig: EnvConfig,
    @Named("push-notification-gateway") pushNotificationGateway: ActorRef,
    venueDao: VenueDao,
    commandDao: CommandDao,
    eventDao: EventDao)(
    implicit ec: ExecutionContext) extends Actor {

  val materializedView = context.actorOf(Props[MaterializedView])

  implicit val askTimeout = envConfig.defaultAskTimeout

  override def receive: Receive = {
    case HandleCommand(command) ⇒
      val result = handleCommand(command)
      result pipeTo sender()

    case RetrieveMaterialized ⇒
      materializedView forward Materialize

    case setState: SetState ⇒
      materializedView forward setState
  }

  private def handleCommand(command: Command) = {
    val actions =
      for {
        venue ← venueDao.retrieve(command.venue.uid) ?! VenueNotFoundByUid(command.venue.uid)
        commandDb ← commandDao.create(commandToCommandDb(command, venue.id))
        events = createEvents(command)
        _ ← eventDao.create(events.map(queueEventToQueueEventDb(commandDb, _)))
      }
      yield {
        materializedView ! EventsBatch(events)
        pushNotificationGateway ! NotifyEvents(command.venue, events)
      }

    db.runTransactionally(actions)
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

  sealed trait CommandProcessorProtocol

  case object RetrieveMaterialized extends CommandProcessorProtocol

  case class HandleCommand(command: Command) extends CommandProcessorProtocol
}
