package com.amplify.api.domain.logic

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(user: AuthenticatedVenueReq): Future[Seq[Playlist]]

  def setCurrentPlaylist(
      venue: AuthenticatedVenueReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit]

  def retrieveCurrentPlaylistTracks(uid: Uid, user: AuthenticatedUserReq): Future[Seq[Track]]

  def retrieveQueue(venue: UnauthenticatedVenue): Future[Queue]

  def retrieveAll(): Future[Seq[Venue]]
}
