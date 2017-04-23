package com.amplify.api.services

import com.amplify.api.domain.models.ContentProviderType.ContentProviderType
import com.amplify.api.domain.models.primitives.Name
import com.amplify.api.domain.models.{Playlist, User, Venue}
import com.amplify.api.services.external.UserData
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[VenueServiceImpl])
trait VenueService {

  def create(
      name: Name[Venue],
      userData: UserData,
      authProviderType: ContentProviderType): Future[Unit]

  def retrievePlaylists(user: User, authToken: String): Future[Seq[Playlist]]
}
