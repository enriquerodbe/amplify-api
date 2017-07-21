package com.amplify.api.domain.logic

import com.amplify.api.domain.models._
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(user: AuthenticatedVenueReq): Future[Seq[PlaylistInfo]]

  def setCurrentPlaylist(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit]

  def retrieveQueue(venue: AuthenticatedVenueReq): Future[Queue]

  def retrieveAll(): Future[Seq[Venue]]
}
