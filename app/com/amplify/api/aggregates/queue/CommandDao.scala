package com.amplify.api.aggregates.queue

import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CommandDaoImpl])
trait CommandDao {

  def create(queueCommand: CommandDb): Future[CommandDb]
}
