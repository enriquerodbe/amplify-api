package com.amplify.api.domain.logic

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(user: VenueReq): Future[Seq[PlaylistInfo]]

  def retrieveCurrentPlaylist(uid: Uid): Future[Option[Playlist]]

  def setCurrentPlaylist(
      venue: VenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit]

  def retrieveQueue(venue: Venue): Future[Queue]

  def retrieveAll(): Future[Seq[Venue]]
}
