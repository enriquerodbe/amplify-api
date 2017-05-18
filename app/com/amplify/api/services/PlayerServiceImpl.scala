package com.amplify.api.services

import com.amplify.api.domain.models.{AuthToken, AuthenticatedVenue, Queue}
import com.amplify.api.services.external.ContentProviderRegistry
import javax.inject.Inject
import scala.concurrent.Future

class PlayerServiceImpl @Inject()(registry: ContentProviderRegistry) extends PlayerService {

  override def play(
      venue: AuthenticatedVenue,
      queue: Queue)(
      implicit authToken: AuthToken): Future[Unit] = {
    val identifier = venue.user.identifier
    val strategy = registry.getStrategy(identifier.contentProvider)
    strategy.play(identifier.identifier, queue.items.map(_.item))
  }
}