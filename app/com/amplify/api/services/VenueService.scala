package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Access, Token, Uid}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: Uid): Future[Option[Venue]]

  def retrieveOrCreate(venueData: VenueData): Future[Venue]

  def retrievePlaylists(venue: Venue)(accessToken: Token[Access]): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(identifier: PlaylistIdentifier)(accessToken: Token[Access]): Future[Playlist]

  def startPlayback(tracks: Seq[TrackIdentifier])(accessToken: Token[Access]): Future[Unit]
}
