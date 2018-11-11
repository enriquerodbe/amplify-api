package com.amplify.api.domain.queue

import com.amplify.api.domain.models.TrackIdentifier
import com.amplify.api.domain.models.primitives.{Access, Token}
import com.amplify.api.domain.playlist.PlaylistExternalContentService
import javax.inject.Inject
import scala.concurrent.Future

class QueueServiceImpl @Inject()(contentService: PlaylistExternalContentService)
  extends QueueService {

  override def startPlayback(
      tracks: Seq[TrackIdentifier])(
      accessToken: Token[Access]): Future[Unit] = contentService.startPlayback(tracks, accessToken)
}
