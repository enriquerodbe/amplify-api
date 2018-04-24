package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Token, Uid}
import com.amplify.api.services.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: Uid): Future[Venue]

  def retrieve(identifier: AuthProviderIdentifier): Future[Option[Venue]]

  def retrieveOrCreate(
      name: Name,
      userData: UserData,
      refreshToken: Token,
      accessToken: Token): Future[Venue]

  def retrievePlaylists(venue: VenueReq): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(venue: VenueReq, identifier: PlaylistIdentifier): Future[Playlist]
}
