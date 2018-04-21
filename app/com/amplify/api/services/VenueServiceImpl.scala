package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.exceptions.VenueNotFoundByUid
import com.amplify.api.services.converters.VenueConverter.{dbVenueToVenue, userDataToDbVenue}
import com.amplify.api.services.external.ContentService
import com.amplify.api.services.models.UserData
import com.amplify.api.utils.DbioUtils.DbioT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    contentService: ContentService,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: Uid): Future[Venue] = {
    val maybeVenue = venueDao.retrieve(uid).map(_.map(dbVenueToVenue))
    db.run(maybeVenue ?! VenueNotFoundByUid(uid))
  }

  override def retrieve(identifier: AuthProviderIdentifier): Future[Option[Venue]] = {
    db.run(venueDao.retrieve(identifier).map(_.map(dbVenueToVenue)))
  }

  override def retrieveOrCreate(userData: UserData, name: Name): Future[Venue] = {
    val dbVenue = userDataToDbVenue(userData, name)
    val createdVenue = db.runTransactionally(venueDao.retrieveOrCreate(dbVenue))
    createdVenue.map(dbVenueToVenue)
  }

  override def retrievePlaylists(venue: VenueReq): Future[Seq[PlaylistInfo]] = {
    implicit val token = venue.authToken
    contentService.fetchPlaylists(venue.contentProviders)
  }

  override def retrievePlaylist(
      venue: VenueReq,
      identifier: PlaylistIdentifier): Future[Playlist] = {
    val eventualPlaylistInfo = retrievePlaylistInfo(venue, identifier)
    val eventualPlaylistTracks = retrievePlaylistTracks(venue, identifier)
    for {
      playlistInfo ← eventualPlaylistInfo
      playlistTracks ← eventualPlaylistTracks
    }
    yield Playlist(playlistInfo, playlistTracks)
  }

  private def retrievePlaylistInfo(
      venue: VenueReq,
      playlistIdentifier: PlaylistIdentifier): Future[PlaylistInfo] = {
    implicit val token = venue.authToken
    contentService.fetchPlaylist(playlistIdentifier)
  }

  private def retrievePlaylistTracks(
      venue: VenueReq,
      playlistIdentifier: PlaylistIdentifier): Future[Seq[Track]] = {
    implicit val token = venue.authToken
    contentService.fetchPlaylistTracks(playlistIdentifier)
  }
}
