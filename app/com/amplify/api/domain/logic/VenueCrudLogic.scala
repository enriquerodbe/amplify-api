package com.amplify.api.domain.logic

import com.amplify.api.domain.models._
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(user: AuthenticatedVenueReq): Future[Seq[Playlist]]

  def setCurrentPlaylist(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit]

  def retrieveQueue(venue: AuthenticatedVenue): Future[Queue]
}
