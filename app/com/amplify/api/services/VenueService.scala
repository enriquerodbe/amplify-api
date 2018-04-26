package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Token, Uid}
import com.amplify.api.services.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: Uid): Future[Option[Venue]]

  def retrieveOrCreate(userData: UserData, refreshToken: Token, accessToken: Token): Future[Venue]

  def retrievePlaylists(venue: Venue): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(venue: Venue, identifier: PlaylistIdentifier): Future[Playlist]
}
