package com.amplify.api.services

import com.amplify.api.domain.models.{AuthenticatedUserReq, AuthenticatedVenue, Playlist, VenueReq}
import com.amplify.api.services.external.models.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def getOrCreate(userData: UserData, venueReq: VenueReq): Future[AuthenticatedVenue]

  def retrievePlaylists(userReq: AuthenticatedUserReq): Future[Seq[Playlist]]
}
