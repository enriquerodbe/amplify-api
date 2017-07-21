package com.amplify.api.command_processors.queue

import com.google.inject.ImplementedBy
import scala.concurrent.Future

@ImplementedBy(classOf[CommandDaoImpl])
trait CommandDao {

  def create(queueCommand: CommandDb): Future[CommandDb]
}
