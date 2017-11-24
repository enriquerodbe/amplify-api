package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.domain.models.primitives.{Name, Token, Uid}
import com.amplify.api.services.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def retrieve(uid: Uid): Future[UnauthenticatedVenue]

  def retrieveOrCreate(userData: UserData, name: Name): Future[AuthenticatedVenue]

  def retrievePlaylists(venue: AuthenticatedVenueReq): Future[Seq[PlaylistInfo]]

  def retrievePlaylist(
      venue: AuthenticatedVenueReq,
      identifier: ContentProviderIdentifier): Future[Playlist]

  def retrieveAll(): Future[Seq[Venue]]

  def setFcmToken(venue: AuthenticatedVenue, token: Token): Future[Unit]
}
