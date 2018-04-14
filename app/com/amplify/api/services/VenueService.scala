package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Uid}
import com.amplify.api.services.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: Uid): Future[Venue]

  def retrieve(identifier: AuthProviderIdentifier): Future[Option[Venue]]

  def retrieveOrCreate(userData: UserData, name: Name): Future[Venue]

  def retrievePlaylists(venue: VenueReq): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(venue: VenueReq, identifier: PlaylistIdentifier): Future[Playlist]
}
