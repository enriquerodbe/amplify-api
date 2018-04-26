package com.amplify.api.domain.logic

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenuePlaylistLogicImpl])
trait VenuePlaylistLogic {

  def retrievePlaylists(user: Venue): Future[Seq[PlaylistInfo]]

  def retrieveCurrentPlaylist(uid: Uid): Future[Option[Playlist]]

  def setCurrentPlaylist(venue: Venue, playlistIdentifier: PlaylistIdentifier): Future[Unit]
}
