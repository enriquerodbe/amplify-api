package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.command_processors.queue.CommandProcessor.RetrieveMaterialized
import com.amplify.api.command_processors.queue.{CommandType, EventType}
import com.amplify.api.domain.models.Queue
import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{QueueCommandDbFixture, QueueEventDbFixture, SpotifyContext, VenueDbFixture}
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenuePlayerControllerSpec extends BaseIntegrationSpec with SpotifyContext with Inside {

  val controller = instanceOf[VenuePlayerController]
  val path = s"/user/queue-command-router/queue-command-processor-$aliceVenueDbId"
  val queueService = app.actorSystem.actorSelection(path)

  class PlayFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with QueueCommandDbFixture

  "play" should {
    "respond No content" in new PlayFixture {
      val response = controller.play()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create queue command" in new PlayFixture {
      controller.play()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDb.id)

      inside(queueCommands) { case Seq(queueCommand) ⇒
          queueCommand must have(
            'userId (None),
            'queueCommandType (CommandType.StartPlayback),
            'contentIdentifier (None)
          )
      }
    }
  }

  class PauseFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with QueueCommandDbFixture

  "pause" should {
    "respond No content" in new PauseFixture {
      val response = controller.pause()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create queue command" in new PauseFixture {
      controller.pause()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDb.id)

      inside(queueCommands) { case Seq(queueCommand) ⇒
          queueCommand must have(
            'userId (None),
            'queueCommandType (CommandType.PausePlayback),
            'contentIdentifier (None)
          )
      }
    }
  }

  class SkipFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends VenueDbFixture with QueueCommandDbFixture with QueueEventDbFixture

  "skip" should {
    "respond No content" in new SkipFixture {
      val response = controller.skip()(FakeRequest().withBody(()).withAliceToken)

      status(response) mustBe NO_CONTENT
    }
    "create queue command" in new SkipFixture {
      controller.skip()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDb.id)

      inside(queueCommands) { case Seq(queueCommand) ⇒
        queueCommand must have(
          'userId (None),
          'queueCommandType (CommandType.SkipCurrentTrack),
          'contentIdentifier (None)
        )
      }
    }
    "create queue events" in new SkipFixture {
      controller.skip()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDbId)
      val queueEvents = findQueueEvents(queueCommands.head.id)

      queueEvents must have size 1
      queueEvents(0) must have(
        'queueCommandId (queueCommands(0).id.value),
        'queueEventType (EventType.CurrentTrackSkipped),
        'contentIdentifier (None)
      )
    }
    "update queue current track" in new SkipFixture {
      controller.skip()(FakeRequest().withBody(()).withAliceToken).await()

      val queue = (queueService ? RetrieveMaterialized).mapTo[Queue].await()

      queue must have(
        'currentTrack (None),
        'items (Nil),
        'position (Nil)
      )
    }
  }
}
