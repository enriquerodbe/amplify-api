package com.amplify.api.shared.controllers

import akka.pattern.ask
import com.amplify.api.domain.models.Spotify.TrackUri
import com.amplify.api.domain.models.{Playlist, Queue, QueueItem, QueueItemType, Spotify}
import com.amplify.api.domain.queue.CommandProcessor.RetrieveState
import com.amplify.api.domain.queue.QueueController
import com.amplify.api.shared.services.external.spotify.Converters.{toModelPlaylist, toModelTrack}
import com.amplify.api.it.fixtures.{DbCoinFixture, DbVenueFixture}
import com.amplify.api.it.{BaseIntegrationSpec, QueueRequests, UserRequests}
import com.amplify.api.shared.exceptions.InvalidProviderIdentifier
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

class QueueControllerSpec
    extends BaseIntegrationSpec with Inside with UserRequests with QueueRequests {

  val controller = instanceOf[QueueController]
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
    "call content provider" in new StartFixture with Eventually {
      await(controller.start()(fakeRequest().withAliceSession))

      eventually(Timeout(3.seconds)) {
        verify(spotifyContentProvider, atLeastOnce())
          .startPlayback(Seq(TrackUri(bedOfNailsTrack.track.id)), aliceAccessToken)
      }
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



  class RetrieveVenueCurrentPlaylistFixture(implicit val dbConfigProvider: DatabaseConfigProvider)
      extends DbVenueFixture with DbCoinFixture

  "retrieveVenueCurrentPlaylist" should {
    "respond empty playlist" when {
      "no playlist was set" in new RetrieveVenueCurrentPlaylistFixture {
        val response =
          controller.retrieveVenueCurrentPlaylist(aliceVenueUid)(FakeRequest().withValidCoin)
        status(response) mustEqual NO_CONTENT
      }
    }

    "respond with some playlist" in new RetrieveVenueCurrentPlaylistFixture {
      val playlist = Playlist(toModelPlaylist(alicePlaylist), Seq.empty)
      val queue = Queue.empty.copy(currentPlaylist = Some(playlist))
      initQueue(aliceVenueUid, queue)

      val response =
        controller.retrieveVenueCurrentPlaylist(aliceVenueUid)(FakeRequest().withValidCoin)

      status(response) mustEqual OK
      contentType(response) must contain (Http.MimeTypes.JSON)
      val jsonResponse = contentAsJson(response)
      (jsonResponse \ "info" \ "name").as[String] mustEqual alicePlaylist.name
      val playlistUri = Spotify.PlaylistUri(aliceSpotifyUser.id, alicePlaylist.id)
      (jsonResponse \ "info" \ "identifier").as[String] mustEqual playlistUri.toString
      (jsonResponse \ "tracks").as[JsArray].value mustBe empty
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
}
