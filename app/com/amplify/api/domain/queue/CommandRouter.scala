package com.amplify.api.domain.queue

import akka.actor.Actor
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.queue.CommandProcessor.{HandleCommand, RetrieveState}
import com.amplify.api.domain.queue.CommandRouter.{RetrieveQueue, RouteCommand}
import javax.inject.Inject
import play.api.libs.concurrent.InjectedActorSupport

private class CommandRouter @Inject()(
    commandProcessorFactory: CommandProcessor.Factory) extends Actor with InjectedActorSupport {

  override def receive: Receive = {
    case RouteCommand(command) ⇒
      val commandProcessor = getCommandProcessor(command.venueUid)
      commandProcessor forward HandleCommand(command)

    case RetrieveQueue(venueUid) ⇒
      val commandProcessor = getCommandProcessor(venueUid)
      commandProcessor forward RetrieveState
  }

  private def getCommandProcessor(venueUid: Uid) = {
    val name = createCommandProcessorName(venueUid)
    val maybeActorRef = context.child(name)
    maybeActorRef.getOrElse {
      injectedChild(commandProcessorFactory(venueUid), name, _.withMailbox("stash-mailbox"))
    }
  }

  private def createCommandProcessorName(venueUid: Uid) = s"queue-command-processor-$venueUid"
}

private object CommandRouter {

  sealed trait CommandRouterProtocol

  case class RouteCommand(command: Command) extends CommandRouterProtocol

  case class RetrieveQueue(venueUid: Uid) extends CommandRouterProtocol
}
