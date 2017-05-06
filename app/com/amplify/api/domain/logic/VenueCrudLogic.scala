package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthenticatedUserReq, ContentProviderIdentifier, Playlist}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueCrudLogicImpl])
trait VenueCrudLogic {

  def retrievePlaylists(user: AuthenticatedUserReq): Future[Seq[Playlist]]


  def setCurrentPlaylist(
      user: AuthenticatedUserReq,
      playlistIdentifier: ContentProviderIdentifier): Future[Unit]
}
