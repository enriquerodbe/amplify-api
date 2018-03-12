package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveMaterialized
import com.amplify.api.aggregates.queue.daos.CommandDbData.{PausePlayback, SkipCurrentTrack, StartPlayback}
import com.amplify.api.aggregates.queue.daos.EventDbData.TrackSkipped
import com.amplify.api.aggregates.queue.daos.{CommandDb, EventDb}
import com.amplify.api.domain.models.Queue
import com.amplify.api.domain.models.primitives.{Id, Uid}
import com.amplify.api.it.BaseIntegrationSpec
import com.amplify.api.it.fixtures.{QueueCommandDbFixture, QueueEventDbFixture, SpotifyContext, VenueDbFixture}
import org.scalatest.Inside
import play.api.db.slick.DatabaseConfigProvider
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenuePlayerControllerSpec extends BaseIntegrationSpec with SpotifyContext with Inside {

  val controller = instanceOf[VenuePlayerController]
  val path = s"/user/queue-command-router/queue-command-processor-$aliceVenueUid"
  val commandProcessor = app.actorSystem.actorSelection(path)

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

      queueCommands must matchPattern {
        case Seq(CommandDb(_, Id(`aliceVenueDbId`), StartPlayback(Uid(`aliceVenueUid`)), _)) ⇒
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

      queueCommands must matchPattern {
        case Seq(CommandDb(_, Id(`aliceVenueDbId`), PausePlayback(Uid(`aliceVenueUid`)), _)) ⇒
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

      queueCommands must matchPattern {
        case Seq(CommandDb(_, Id(`aliceVenueDbId`), SkipCurrentTrack(Uid(`aliceVenueUid`)), _)) ⇒
      }
    }
    "create queue events" in new SkipFixture {
      controller.skip()(FakeRequest().withBody(()).withAliceToken).await()

      val queueCommands = findQueueCommands(aliceVenueDbId)
      val queueEvents = findQueueEvents(queueCommands.head.id)

      val commandId = queueCommands.head.id
      queueEvents must matchPattern {
        case Seq(EventDb(_, `commandId`, TrackSkipped, _)) ⇒
      }
    }
    "update queue current track" in new SkipFixture {
      controller.skip()(FakeRequest().withBody(()).withAliceToken).await()

      val queue = (commandProcessor ? RetrieveMaterialized).mapTo[Queue].await()

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }
}
