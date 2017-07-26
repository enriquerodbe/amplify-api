package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveMaterialized
import com.amplify.api.aggregates.queue.CommandRouter.RetrieveQueue
import com.amplify.api.domain.models.AuthenticatedVenueReq
import com.amplify.api.domain.models.primitives.Id
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
  }

  private def getCommandProcessor(venueId: Id) = {
    val name = createCommandProcessorName(venueId)
    context.child(name).getOrElse(createCommandProcessor(venueId))
  }

  private def createCommandProcessorName(venueId: Id) = s"queue-command-processor-$venueId"

  private def createCommandProcessor(venueId: Id) = {
    val name = createCommandProcessorName(venueId)
    injectedChild(commandProcessorFactory(), name)
  }
}

object CommandRouter {

  case class RetrieveQueue(venue: AuthenticatedVenueReq)
}
