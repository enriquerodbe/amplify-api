package com.amplify.api.aggregates.queue

import akka.actor.Actor
import com.amplify.api.aggregates.queue.CommandProcessor.{HandleCommand, RetrieveState}
import com.amplify.api.aggregates.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import com.amplify.api.domain.models.Venue
import com.amplify.api.domain.models.primitives.Uid
import javax.inject.Inject
import play.api.libs.concurrent.InjectedActorSupport

class CommandRouter @Inject()(
    commandProcessorFactory: CommandProcessor.Factory) extends Actor with InjectedActorSupport {

  override def receive: Receive = {
    case RouteCommand(command) ⇒
      val commandProcessor = getCommandProcessor(command.venue)
      commandProcessor forward HandleCommand(command)

    case RetrieveQueue(venue) ⇒
      val commandProcessor = getCommandProcessor(venue)
      commandProcessor forward RetrieveState
  }

  private def getCommandProcessor(venue: Venue) = {
    val name = createCommandProcessorName(venue.uid)
    val maybeActorRef = context.child(name)
    maybeActorRef.getOrElse {
      injectedChild(commandProcessorFactory(venue), name, _.withMailbox("stash-mailbox"))
    }
  }

  private def createCommandProcessorName(venueUid: Uid) = s"queue-command-processor-$venueUid"
}

object CommandRouter {

  sealed trait CommandRouterProtocol

  case class RouteCommand(command: Command) extends CommandRouterProtocol

  case class RetrieveQueue(venue: Venue) extends CommandRouterProtocol
}
