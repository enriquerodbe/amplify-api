package com.amplify.api.services

import com.amplify.api.daos.{DbioRunner, UserDao, VenueDao}
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.exceptions.VenueNotFoundByUid
import com.amplify.api.services.converters.PlaylistConverter.playlistDataToPlaylistInfo
import com.amplify.api.services.converters.TrackConverter.trackDataToTrack
import com.amplify.api.services.converters.VenueConverter.{userDataToVenueDb, venueDbToVenue}
import com.amplify.api.services.external.ContentProviderRegistry
import com.amplify.api.services.models.UserData
import com.amplify.api.utils.DbioUtils.DbioT
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueServiceImpl @Inject()(
    db: DbioRunner,
    registry: ContentProviderRegistry,
    userDao: UserDao,
    venueDao: VenueDao)(
    implicit ec: ExecutionContext) extends VenueService {

  override def retrieve(uid: Uid): Future[Venue] = {
    val maybeVenue = venueDao.retrieve(uid).map(_.map(venueDbToVenue))
    db.run(maybeVenue ?! VenueNotFoundByUid(uid))
  }

  override def retrieve(identifier: AuthProviderIdentifier): Future[Option[Venue]] = {
    db.run(venueDao.retrieve(identifier).map(_.map(venueDbToVenue)))
  }

  override def retrieveOrCreate(userData: UserData, name: Name): Future[Venue] = {
    val venueDb = userDataToVenueDb(userData, name)
    val createdVenue = db.runTransactionally(venueDao.retrieveOrCreate(venueDb))
    createdVenue.map(venueDbToVenue)
  }

  override def retrievePlaylists(venue: VenueReq): Future[Seq[PlaylistInfo]] = {
    val strategy = registry.getStrategy(venue.contentProviders)
    val eventualPlaylists = strategy.fetchPlaylists(venue.authToken)
    eventualPlaylists.map(_.map(playlistDataToPlaylistInfo))
  }

  override def retrievePlaylist(
      venue: VenueReq,
      identifier: ContentProviderIdentifier): Future[Playlist] = {
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
      playlistIdentifier: ContentProviderIdentifier): Future[PlaylistInfo] = {
    implicit val token = venue.authToken
    val contentProvider = registry.getStrategy(playlistIdentifier.contentProvider)
    val userIdentifier = venue.identifier.identifier
    val result = contentProvider.fetchPlaylist(userIdentifier, playlistIdentifier.identifier)
    result.map(playlistDataToPlaylistInfo)
  }

  private def retrievePlaylistTracks(
      venue: VenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Seq[Track]] = {
    implicit val token = venue.authToken
    val strategy = registry.getStrategy(playlistIdentifier.contentProvider)
    val userIdentifier = venue.identifier.identifier
    val eventualPlaylist =
      strategy.fetchPlaylistTracks(userIdentifier, playlistIdentifier.identifier)
    eventualPlaylist.map(_.map(trackDataToTrack))
  }

  override def retrieveAll(): Future[Seq[Venue]] = {
    db.run(venueDao.retrieveAll()).map(_.map(venueDbToVenue))
  }
}
