package com.amplify.api.services

import com.amplify.api.domain.models._
import com.amplify.api.services.external.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def getOrCreate(userData: UserData, venueReq: VenueReq): Future[AuthenticatedVenue]

  def retrievePlaylists(user: AuthenticatedUserReq): Future[Seq[Playlist]]

  def setCurrentPlaylist(
      user: AuthenticatedUserReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit]
}
