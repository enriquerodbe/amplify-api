package com.amplify.api.controllers

import akka.pattern.ask
import com.amplify.api.aggregates.queue.CommandProcessor.RetrieveState
import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models.{Playlist, Queue, QueueItem, QueueItemType}
import com.amplify.api.it.fixtures.{DbCoinFixture, DbVenueFixture}
import com.amplify.api.it.{BaseIntegrationSpec, UserRequests}
import com.amplify.api.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import org.mockito.Mockito.{atLeastOnce, verify, inOrder ⇒ order}
import org.scalatest.Inside
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsArray, JsDefined}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http
import scala.concurrent.duration.DurationLong

class VenueQueueControllerSpec extends BaseIntegrationSpec with Inside with UserRequests {

  val controller = instanceOf[VenueQueueController]
  val commandProcessor = findCommandProcessor(aliceVenueUid)

  class StartFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture {
    val newQueue =
      Queue.empty.copy(
        futureItems = List(QueueItem(toModelTrack(bedOfNailsTrack), QueueItemType.Venue)))
    initQueue(aliceVenueUid, newQueue)
  }

  "start" should {
    "respond No content" in new StartFixture {
      val response = controller.start()(fakeRequest().withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "call content provider" in new StartFixture {
      await(controller.start()(fakeRequest().withAliceSession))

      verify(spotifyContentProvider, atLeastOnce())
        .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), aliceAccessToken)
    }
    "refresh tokens" when {
      "access token expires" in new StartFixture with Eventually {
        import profile.api._
        await(db.run {
          venuesTable.filter(_.id === aliceDbVenue.id).map(_.accessToken).update(invalidAccessToken)
        })

        await(controller.start()(fakeRequest().withAliceSession))

        eventually(Timeout(3.seconds)) {
          val calls = order(spotifyContentProvider, spotifyAuthProvider, spotifyContentProvider)
          calls.verify(spotifyContentProvider)
            .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), invalidAccessToken)
          calls.verify(spotifyAuthProvider).refreshAccessToken(aliceRefreshToken)
          calls.verify(spotifyContentProvider)
            .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), aliceAccessToken)
        }
      }
    }
  }

  class SkipFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends DbVenueFixture

  "skip" should {
    "respond No content" in new SkipFixture {
      val response = controller.skip()(fakeRequest().withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new SkipFixture {
      await(controller.skip()(fakeRequest().withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }

  class FinishFixture(implicit val dbConfigProvider: DatabaseConfigProvider) extends DbVenueFixture

  "finish" should {
    "respond No content" in new FinishFixture {
      val response = controller.finish()(fakeRequest().withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new FinishFixture {
      await(controller.finish()(fakeRequest().withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }

  class AddTrackFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture {
    val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq(toModelTrack(bedOfNailsTrack)))
    val newQueue = Queue.empty.copy(currentPlaylist = Some(playlist))
    initQueue(aliceVenueUid, newQueue)
  }

  "addTrack" should {
    "respond No content" in new AddTrackFixture {
      val request = addTrackRequest(TrackUri(bedOfNailsTrack.track.id)).withValidCoin
      val response = controller.addTrack(aliceVenueUid)(request)
      status(response) mustBe NO_CONTENT
    }
    "update queue next track" in new AddTrackFixture {
      val trackId = TrackUri(bedOfNailsTrack.track.id)
      val request = addTrackRequest(trackId).withValidCoin
      await(controller.addTrack(aliceVenueUid)(request))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      val nextItem = queue.futureItems.head
      nextItem.isUserTrack mustBe true
      nextItem.track.identifier mustEqual trackId
    }
  }

  class RetrieveQueueFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "retrieveQueue" should {
    "respond OK" in new RetrieveQueueFixture {
      val response = controller.retrieveQueue()(FakeRequest().withBody(()).withAliceSession)
      status(response) mustBe OK
    }

    "respond with queue" in new RetrieveQueueFixture {
      val response = controller.retrieveQueue()(FakeRequest().withBody(()).withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)

      (jsonResponse \ "tracks") mustEqual JsDefined(JsArray(Seq.empty))
    }
  }
}
