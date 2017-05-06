package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthenticatedUserReq, ContentProviderIdentifier, Playlist}
import com.amplify.api.services.VenueService
import javax.inject.Inject
import scala.concurrent.Future

class VenueCrudLogicImpl @Inject()(venueService: VenueService) extends VenueCrudLogic {

  override def retrievePlaylists(user: AuthenticatedUserReq): Future[Seq[Playlist]] = {
    venueService.retrievePlaylists(user)
  }

  override def setCurrentPlaylist(
      user: AuthenticatedUserReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit] = {
    venueService.setCurrentPlaylist(user, playlistIdentifier)
    // Retrieve playlist data and update the venue's queue
  }
}
