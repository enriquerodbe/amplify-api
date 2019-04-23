package com.amplify.api.domain.venue

import akka.pattern.ask
import com.amplify.api.domain.coin.DbCoinFixture
import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.amplify.api.domain.queue.CommandProcessor.RetrieveState
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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http
import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class VenueControllerSpec extends BaseIntegrationSpec with Inside with VenueRequests {

  val controller = instanceOf[VenueController]
  val commandProcessor = findCommandProcessor(aliceVenueUid)
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
        code must have size 25
        code must startWith (s"$aliceVenueUid:")
      }
    }
    "create coins" in new CreateCoinsFixture {
      await(controller.createCoins()(createCoinsRequest(validRequestNumber).withAliceSession))

      val createdCoins = findCoins(aliceVenueUid)

      createdCoins must have size validRequestNumber + 1
      forAll(createdCoins) { coin ⇒
        coin.coinCode.toString must have size 25
        coin.coinCode.toString must startWith (s"$aliceVenueUid:")
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
      val response = controller.retrievePlaylists()(FakeRequest().withBody(()).withAliceSession)
      status(response) mustEqual OK
    }
    "respond with playlists" in new RetrievePlaylistsFixture {
      val response = controller.retrievePlaylists()(FakeRequest().withBody(()).withAliceSession)

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
          await(controller.retrievePlaylists()(FakeRequest().withBody(()).withAliceSession))
        }
      }
    }
  }

  class SetCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "setCurrentPlaylist" should {
    "respond No content" in new SetCurrentPlaylistFixture {
      val response = controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession)
      status(response) mustEqual NO_CONTENT
    }
    "update queue current playlist" in new SetCurrentPlaylistFixture {
      await(controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      inside(queue.currentPlaylist) { case Some(Playlist(playlistInfo, tracks)) ⇒
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
    "update queue current track" in new SetCurrentPlaylistFixture {
      await(controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      inside(queue.currentItem) { case Some(QueueItem(track, itemType)) ⇒
        itemType mustEqual QueueItemType.Venue
        track must have(
          'name (alicePlaylistTracks.head.track.name.toString),
          'identifier (TrackUri(alicePlaylistTracks.head.track.id))
        )
      }
    }
    "update queue items" in new SetCurrentPlaylistFixture {
      await(controller.setCurrentPlaylist()(
        playlistRequest(alicePlaylistUri.toString).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      for (item ← queue.allItems) item must have ('itemType (QueueItemType.Venue))
      queue.allItems.map(_.track) must contain theSameElementsAs queue.currentPlaylist.get.tracks
    }

    "fail" when {
      "invalid identifier" in new SetCurrentPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          await(controller.setCurrentPlaylist()(
            playlistRequest("wrong_identifier").withAliceSession))
        }
      }
      "invalid content provider" in new SetCurrentPlaylistFixture {
        intercept[InvalidProviderIdentifier] {
          await(controller.setCurrentPlaylist()(
            playlistRequest("wrong_provider:wrong_identifier").withAliceSession))
        }
      }
    }
  }

  class RetrieveCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture

  "retrieveCurrentPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveCurrentPlaylistFixture {
        val response =
          controller.retrieveCurrentPlaylist()(FakeRequest().withBody(()).withAliceSession)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveCurrentPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(currentPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response =
        controller.retrieveCurrentPlaylist()(FakeRequest().withBody(()).withAliceSession)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
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

  class StartFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
    extends DbVenueFixture with DbCoinFixture {
    val newQueue =
      Queue.empty.copy(
        futureItems = List(QueueItem(toModelTrack(bedOfNailsTrack), QueueItemType.Venue)))
    initQueue(aliceVenueUid, newQueue)
  }

  "start" should {
    "respond No content" in new StartFixture {
      val response = controller.start()(FakeRequest().withBody(()).withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "call content provider" in new StartFixture with Eventually {
      await(controller.start()(FakeRequest().withBody(()).withAliceSession))

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

        await(controller.start()(FakeRequest().withBody(()).withAliceSession))

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
      val response = controller.skip()(FakeRequest().withBody(()).withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new SkipFixture {
      await(controller.skip()(FakeRequest().withBody(()).withAliceSession))

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
      val response = controller.finish()(FakeRequest().withBody(()).withAliceSession)
      status(response) mustBe NO_CONTENT
    }
    "update queue current track" in new FinishFixture {
      await(controller.finish()(FakeRequest().withBody(()).withAliceSession))

      val queue = await((commandProcessor ? RetrieveState).mapTo[Queue])

      queue must have(
        'currentItem (None),
        'futureItems (Nil),
        'pastItems (Nil)
      )
    }
  }
}
