package com.amplify.api.domain.queue

import com.amplify.api.domain.models.TrackIdentifier
import com.amplify.api.domain.models.primitives.{Access, Token}
import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[QueueServiceImpl])
trait QueueService {

  def startPlayback(tracks: Seq[TrackIdentifier])(accessToken: Token[Access]): Future[Unit]
}
