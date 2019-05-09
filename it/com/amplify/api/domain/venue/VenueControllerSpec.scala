package com.amplify.api.domain.venue

import com.amplify.api.domain.coin.DbCoinFixture
import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.queue.QueueService
import com.amplify.api.it.{BaseIntegrationSpec, VenueRequests}
import com.amplify.api.shared.configuration.EnvConfig
import com.amplify.api.shared.exceptions.{InvalidCreateCoinsRequestedNumber, InvalidProviderIdentifier, UnexpectedResponse}
import com.amplify.api.shared.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import org.mockito.Mockito.{atLeastOnce, verify, when, inOrder ⇒ order}
import org.scalatest.Inside
import org.scalatest.Inspectors.forAll
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsArray, JsDefined}
import play.api.test.Helpers._
import play.mvc.Http
import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class VenueControllerSpec extends BaseIntegrationSpec with Inside with VenueRequests {

  val controller = instanceOf[VenueController]
  val queueService = instanceOf[QueueService]
  val envConfig = instanceOf[EnvConfig]
  val validRequestNumber = 2

  class CreateCoinsFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture with DbCoinFixture

  "createCoins" should {
    "respond OK" in new CreateCoinsFixture {
      val response =
        controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession)
      status(response) mustEqual OK
    }
    "respond with coins" in new CreateCoinsFixture {
      val response =
        controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response).as[JsArray]
      jsonResponse.value.foreach { jsValue ⇒
        (jsValue \ "remaining").as[Int] mustBe 1
        val code = (jsValue \ "code").as[String]
        code must have size 4
      }
    }
    "create coins" in new CreateCoinsFixture {
      await(controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession))

      val createdCoins = findCoins(aliceVenueUid)

      createdCoins must have size validRequestNumber + 2
      forAll(createdCoins) { coin ⇒
        coin.code.toString must have size 4
        coin.venueUid.toString must be (aliceVenueUid)
        coin.maxUsages mustBe envConfig.coinsDefaultMaxUsages
      }
    }
    "fail" when {
      "zero requested" in new CreateCoinsFixture {
        intercept[InvalidCreateCoinsRequestedNumber] {
          await(controller.createCoins()(createCoinsRequest(0).withAliceSession))
        }
      }
      "more than the maximum requested" in new CreateCoinsFixture {
        val requestNumber = envConfig.coinsCreateMax + 1
        intercept[InvalidCreateCoinsRequestedNumber] {
          await(controller.createCoins()(createCoinsRequest(requestNumber).withAliceSession))
        }
      }
    }
  }

  class RetrievePlaylistsFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "retrievePlaylists" should {
    "respond OK" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(emptyRequest().withAliceSession)
      status(response) mustEqual OK
    }
    "respond with playlists" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(emptyRequest().withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response).head
      (jsonResponse \ "name").as[String] mustEqual alicePlaylist.name.toString
      (jsonResponse \ "identifier").as[String] mustEqual alicePlaylistUri.toString
      val image = (jsonResponse \ "images").head
      (image \ "url").as[String] mustEqual alicePlaylistImages.head.url
      (image \ "height").as[Int] mustEqual alicePlaylistImages.head.height.get
      (image \ "width").as[Int] mustEqual alicePlaylistImages.head.width.get
    }

    "fail" when {
      "Spotify responds with unexpected response" in new RetrievePlaylistsFixture {
        when(spotifyContentProvider.fetchPlaylists(aliceAccessToken))
            .thenReturn(Future.failed(UnexpectedResponse("Testing!")))

        intercept[UnexpectedResponse] {
          await(controller.retrievePlaylists()(emptyRequest().withAliceSession))
        }
      }
    }
  }

  class RetrievePlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "retrievePlaylist" should {
    "respond OK" in new RetrievePlaylistFixture {
      val response =
        controller.retrievePlaylist(alicePlaylistUri.toString)(emptyRequest().withAliceSession)
      status(response) mustEqual OK
    }
    "respond with some playlist" in new RetrievePlaylistFixture {
      val response =
        controller.retrievePlaylist(alicePlaylistUri.toString)(emptyRequest().withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustNot be(empty)
    }
  }

  class SetAllowedPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "setAllowedPlaylist" should {
    "respond No content" in new SetAllowedPlaylistFixture {
      val response = controller.setAllowedPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession)
      status(response) mustEqual NO_CONTENT
    }
    "update queue current playlist" in new SetAllowedPlaylistFixture {
      await(controller.setAllowedPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      inside(queue.allowedPlaylist) { case Some(Playlist(playlistInfo, tracks)) ⇒
        playlistInfo must have(
          'name (alicePlaylist.name),
          'identifier (alicePlaylistUri)
        )
        playlistInfo.images must have size alicePlaylist.images.size
        for (i ← playlistInfo.images.indices) {
          val playlistImage = alicePlaylist.images(i)
          playlistInfo.images(i) must have(
            'url (playlistImage.url),
            'width (playlistImage.width),
            'height (playlistImage.height)
          )
        }

        tracks must have size alicePlaylistTracks.size
        for (trackIndex ← tracks.indices) {
          val playlistTrack = alicePlaylistTracks(trackIndex)
          tracks(trackIndex) must have(
            'name (playlistTrack.track.name),
            'identifier (TrackUri(playlistTrack.track.id))
          )
          tracks(trackIndex).album must have('name (playlistTrack.track.album.name))
          tracks(trackIndex).album.artists must have size playlistTrack.track.album.artists.size
          for (artistIndex ← tracks(trackIndex).album.artists.indices) {
            tracks(trackIndex).album.artists(artistIndex) must have(
              'name (playlistTrack.track.album.artists(artistIndex).name)
            )
          }
          tracks(trackIndex).album.images must have size playlistTrack.track.album.images.size
          for (imageIndex ← tracks(trackIndex).album.images.indices) {
            tracks(trackIndex).album.images(imageIndex) must have(
              'url (playlistTrack.track.album.images(imageIndex).url),
              'width (playlistTrack.track.album.images(imageIndex).width),
              'height (playlistTrack.track.album.images(imageIndex).height)
            )
          }
        }
      }
    }

    "fail" when {
      "invalid identifier" in new SetAllowedPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          await(controller.setAllowedPlaylist()(
            playlistRequest("wrong_identifier").withAliceSession))
        }
      }
      "invalid content provider" in new SetAllowedPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          await(controller.setAllowedPlaylist()(
            playlistRequest("wrong_provider:wrong_identifier").withAliceSession))
        }
      }
    }
  }

  class RetrieveAllowedPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "retrieveAllowedPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveAllowedPlaylistFixture {
        val response =
          controller.retrieveAllowedPlaylist()(emptyRequest().withAliceSession)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveAllowedPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(allowedPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response = controller.retrieveAllowedPlaylist()(emptyRequest().withAliceSession)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
    }
  }

  class AddPlaylistTracksFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "addPlaylistTracks" should {
    "respond No content" in new AddPlaylistTracksFixture {
      val response = controller.addPlaylistTracks()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession)
      status(response) mustEqual NO_CONTENT
    }
    "update queue current track" in new AddPlaylistTracksFixture {
      await(controller.addPlaylistTracks()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      inside(queue.currentItem) { case Some(QueueItem(track, itemType)) ⇒
        itemType mustEqual QueueItemType.Venue
        track must have(
          'name (alicePlaylistTracks.head.track.name.toString),
          'identifier (TrackUri(alicePlaylistTracks.head.track.id))
        )
      }
    }
    "update queue items" in new AddPlaylistTracksFixture() {
      await(controller.addPlaylistTracks()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      for (item ← queue.allItems) item must have ('itemType (QueueItemType.Venue))
      queue.allItems.map(_.track) must contain theSameElementsAs
          alicePlaylistTracks.map(toModelTrack)
    }
  }

  class AddVenueTrackFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture

  "addVenueTrack" should {
    "respond No content" in new AddVenueTrackFixture() {
      val response = controller.addVenueTrack()(
        addTrackRequest(TrackUri(bedOfNailsTrack.track.id)).withAliceSession)
      status(response) mustEqual NO_CONTENT
    }
    "update queue current track" in new AddVenueTrackFixture {
      val trackId = TrackUri(bedOfNailsTrack.track.id)
      val request = addTrackRequest(trackId).withAliceSession
      await(controller.addVenueTrack()(request))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      val nextItem = queue.currentItem.get
      nextItem.isUserTrack mustBe false
      nextItem.track.identifier mustEqual trackId
    }
  }

  class RetrieveQueueFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "retrieveQueue" should {
    "respond OK" in new RetrieveQueueFixture {
      val response = controller.retrieveQueue()(emptyRequest().withAliceSession)
      status(response) mustBe OK
    }

    "respond with queue" in new RetrieveQueueFixture {
      val response = controller.retrieveQueue()(emptyRequest().withAliceSession)

      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)

      (jsonResponse \ "tracks") mustEqual JsDefined(JsArray(Seq.empty))
    }
  }

  class StartFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture {
    val newQueue =
      Queue.empty.copy(
        futureItems = List(QueueItem(toModelTrack(bedOfNailsTrack), QueueItemType.Venue)))
    initQueue(aliceVenueUid, newQueue)
  }

  "start" should {
    "respond No content" in new StartFixture {
      val response = controller.start()(emptyRequest().withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "call content provider" in new StartFixture with Eventually {
      await(controller.start()(emptyRequest().withAliceSession))

      eventually(Timeout(3.seconds)) {
        verify(spotifyContentProvider, atLeastOnce())
          .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), aliceAccessToken)
      }
    }
    "refresh tokens" when {
      "access token expires" in new StartFixture with Eventually {
        import profile.api._
        await(db.run {
          venuesTable
              .filter(_.uid === Uid(aliceVenueUid))
              .map(_.accessToken)
              .update(invalidAccessToken)
        })

        await(controller.start()(emptyRequest().withAliceSession))

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
      val response = controller.skip()(emptyRequest().withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new SkipFixture {
      await(controller.skip()(emptyRequest().withAliceSession))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

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
      val response = controller.finish()(emptyRequest().withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new FinishFixture {
      await(controller.finish()(emptyRequest().withAliceSession))

      val queue = await(queueService.retrieveQueue(aliceVenueUid))

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }
}
