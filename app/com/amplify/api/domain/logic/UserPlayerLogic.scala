package com.amplify.api.domain.logic

import com.amplify.api.domain.models.{AuthenticatedUser, ContentProviderIdentifier}
import com.amplify.api.domain.models.primitives.Uid
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[UserPlayerLogicImpl])
trait UserPlayerLogic {

  def addTrack(
      venueUid: Uid,
      user: AuthenticatedUser,
      trackIdentifier: ContentProviderIdentifier): Future[Unit]
}
