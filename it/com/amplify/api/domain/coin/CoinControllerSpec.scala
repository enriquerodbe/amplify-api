package com.amplify.api.domain.coin

import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models.{Playlist, Queue, Spotify}
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.domain.venue.DbVenueFixture
import com.amplify.api.it.{BaseIntegrationSpec, CoinRequests}
import com.amplify.api.shared.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http

class CoinControllerSpec extends BaseIntegrationSpec with CoinRequests {

  val controller = instanceOf[CoinController]
  val queueService = instanceOf[QueueService]
  val commandProcessor = findCommandProcessor(aliceVenueUid)

  class CoinStatusFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture with DbCoinFixture

  "coinStatus" should {
    "respond OK" in new CoinStatusFixture {
      val response = controller.coinStatus()(FakeRequest().withBody(()).withValidCoin)
      status(response) mustEqual OK
    }
    "respond with status" in new CoinStatusFixture {
      val response = controller.coinStatus()(FakeRequest().withBody(()).withValidCoin)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "venue_name").as[String] mustEqual aliceDbVenue.name.value
      (jsonResponse \ "remaining_usages").as[Int] mustEqual 1
    }
  }

  class AddTrackFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture with DbCoinFixture {
    val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq(toModelTrack(bedOfNailsTrack)))
    val newQueue = Queue.empty.copy(allowedPlaylist = Some(playlist))
    initQueue(aliceVenueUid, newQueue)
  }

  "addTrack" should {
    "respond No content" in new AddTrackFixture {
      val request = addTrackRequest(TrackUri(bedOfNailsTrack.track.id)).withValidCoin
      val response = controller.addTrack()(request)
      status(response) mustBe NO_CONTENT
    }
    "update queue next track" in new AddTrackFixture {
      val trackId = TrackUri(bedOfNailsTrack.track.id)
      val request = addTrackRequest(trackId).withValidCoin
      await(controller.addTrack()(request))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      val nextItem = queue.futureItems.head
      nextItem.isUserTrack mustBe true
      nextItem.track.identifier mustEqual trackId
    }
  }

  class RetrieveCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture with DbCoinFixture

  "retrieveCurrentPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveCurrentPlaylistFixture {
        val response =
          controller.retrieveCurrentPlaylist()(FakeRequest().withValidCoin)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveCurrentPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(allowedPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response =
        controller.retrieveCurrentPlaylist()(FakeRequest().withValidCoin)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
    }
  }
}
