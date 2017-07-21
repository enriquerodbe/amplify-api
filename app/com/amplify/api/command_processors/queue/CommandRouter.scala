package com.amplify.api.command_processors.queue

import akka.actor.Actor
import com.amplify.api.command_processors.queue.CommandProcessor.RetrieveMaterialized
import com.amplify.api.command_processors.queue.CommandRouter.RetrieveQueue
import com.amplify.api.daos.primitives.Id
import com.amplify.api.domain.models.{AuthenticatedVenueReq, Venue}
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

  private def getCommandProcessor(venueId: Id[Venue]) = {
    val name = createCommandProcessorName(venueId)
    context.child(name).getOrElse(createCommandProcessor(venueId))
  }

  private def createCommandProcessorName(venueId: Id[Venue]) = s"queue-command-processor-$venueId"

  private def createCommandProcessor(venueId: Id[Venue]) = {
    val name = createCommandProcessorName(venueId)
    injectedChild(commandProcessorFactory(venueId), name)
  }
}

object CommandRouter {

  case class RetrieveQueue(venue: AuthenticatedVenueReq)
}
