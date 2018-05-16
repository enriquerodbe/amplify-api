package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: Uid): Future[Option[Venue]]

  def retrieveOrCreate(venueData: VenueData): Future[Venue]

  def retrievePlaylists(venue: Venue): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(venue: Venue, identifier: PlaylistIdentifier): Future[Playlist]

  def startPlayback(venue: Venue, tracks: Seq[TrackIdentifier]): Future[Unit]
}
