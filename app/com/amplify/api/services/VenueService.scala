package com.amplify.api.services

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Token
import com.amplify.api.services.external.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: String): Future[UnauthenticatedVenue]

  def retrieveOrCreate(userData: UserData, venueReq: VenueRequest): Future[AuthenticatedVenue]

  def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[PlaylistInfo]]

  def retrievePlaylistInfo(
      venueReq: AuthenticatedVenueReq,
      identifier: ContentProviderIdentifier): Future[PlaylistInfo]

  def retrievePlaylistTracks(
      venue: AuthenticatedVenueReq,
      identifier: ContentProviderIdentifier): Future[Seq[Track]]

  def retrievePlaylist(
      venue: AuthenticatedVenueReq,
      identifier: ContentProviderIdentifier): Future[Playlist]

  def retrieveAll(): Future[Seq[Venue]]

  def setFcmToken(venue: AuthenticatedVenue, token: Token): Future[Unit]
}
