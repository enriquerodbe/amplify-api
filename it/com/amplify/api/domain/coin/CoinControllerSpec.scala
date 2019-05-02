package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models.{Playlist, Queue, Spotify}
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.DbVenueFixture
import com.amplify.api.it.{BaseIntegrationSpec, CoinRequests}
import com.amplify.api.shared.exceptions.CoinMaxUsages
import com.amplify.api.shared.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http

class CoinControllerSpec extends BaseIntegrationSpec with CoinRequests {

  val controller = instanceOf[CoinController]
  val queueService = instanceOf[QueueService]

  class RetrieveAllowedPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture

  "retrieveAllowedPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveAllowedPlaylistFixture {
        val response =
          controller.retrieveAllowedPlaylist()(FakeRequest().withUnusedCoin)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveAllowedPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(allowedPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response = controller.retrieveAllowedPlaylist()(FakeRequest().withUnusedCoin)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
    }
  }

  class RetrieveRemainingUsagesFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture

  "retrieveRemainingUsages" should {
    "respond OK" in new RetrieveRemainingUsagesFixture {
      val response = controller.retrieveRemainingUsages()(FakeRequest().withUnusedCoin)
      status(response) mustEqual OK
    }
    "respond 1 for unused coin" in new RetrieveRemainingUsagesFixture {
      val response = controller.retrieveRemainingUsages()(FakeRequest().withUnusedCoin)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "remaining").as[Int] mustEqual 1
    }
    "respond 0 for used coin" in new RetrieveRemainingUsagesFixture {
      val response = controller.retrieveRemainingUsages()(FakeRequest().withUsedCoin)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "remaining").as[Int] mustEqual 0
    }
  }

  class RetrieveCurrentTrackFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture

  class AddTrackFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture with DbCoinFixture {
    val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq(toModelTrack(bedOfNailsTrack)))
    val newQueue = Queue.empty.copy(allowedPlaylist = Some(playlist))
    initQueue(aliceVenueUid, newQueue)
  }

  "addTrack" should {
    "respond No content" in new AddTrackFixture {
      val request = addTrackRequest(TrackUri(bedOfNailsTrack.track.id)).withUnusedCoin
      val response = controller.addTrack()(request)
      status(response) mustBe NO_CONTENT
    }
    "update queue next track" in new AddTrackFixture {
      val trackId = TrackUri(bedOfNailsTrack.track.id)
      val request = addTrackRequest(trackId).withUnusedCoin
      await(controller.addTrack()(request))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      val nextItem = queue.futureItems.head
      nextItem.isUserTrack mustBe true
      nextItem.track.identifier mustEqual trackId
    }
    "create coin usage" in new AddTrackFixture {
      val request = addTrackRequest(TrackUri(bedOfNailsTrack.track.id)).withUnusedCoin
      val response = controller.addTrack()(request)
      status(response) mustBe NO_CONTENT
      val coinUsages = findCoinUsages(unusedCoin.id)
      coinUsages must have size 1
      coinUsages.head must have(
        'usageNumber (1)
      )
    }
    "reject used coin" in new AddTrackFixture {
      val trackId = TrackUri(bedOfNailsTrack.track.id)
      val request = addTrackRequest(trackId).withUsedCoin

      intercept[CoinMaxUsages](await(controller.addTrack()(request)))
    }
  }
}
