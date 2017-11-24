package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveMaterialized
import com.amplify.api.aggregates.queue.CommandRouter.{RetrieveCurrentPlaylist, RetrieveQueue}
import com.amplify.api.domain.models.primitives.Id
import com.amplify.api.domain.models.{AuthenticatedVenueReq, UnauthenticatedVenue}
import javax.inject.Inject
import play.api.libs.concurrent.InjectedActorSupport

class CommandRouter @Inject()(
    commandProcessorFactory: CommandProcessor.Factory) extends Actor with InjectedActorSupport {

  override def receive: Receive = {
    case command: CommandProcessor.Command ⇒
      val commandProcessor = getCommandProcessor(command.venue.id)
      commandProcessor forward command

    case RetrieveQueue(venue) ⇒
      val commandProcessor = getCommandProcessor(venue.id)
      commandProcessor forward RetrieveMaterialized

    case RetrieveCurrentPlaylist(venue) ⇒
      val commandProcessor = getCommandProcessor(venue.id)
      commandProcessor forward CommandProcessor.RetrieveCurrentPlaylist
  }

  private def getCommandProcessor(venueId: Id) = {
    val name = createCommandProcessorName(venueId)
    context.child(name).getOrElse(injectedChild(commandProcessorFactory(), name))
  }

  private def createCommandProcessorName(venueId: Id) = s"queue-command-processor-$venueId"
}

object CommandRouter {

  case class RetrieveQueue(venue: AuthenticatedVenueReq)

  case class RetrieveCurrentPlaylist(venue: UnauthenticatedVenue)
}