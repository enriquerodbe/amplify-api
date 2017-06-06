package com.amplify.api.services

import com.amplify.api.controllers.dtos.Venue.VenueRequest
import com.amplify.api.domain.models._
import com.amplify.api.services.external.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def getOrCreate(userData: UserData, venueReq: VenueRequest): Future[AuthenticatedVenue]

  def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[Playlist]]

  def retrievePlaylistTracks(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Seq[Track]]

  def retrieveAllVenues(): Future[Seq[Venue]]
}
