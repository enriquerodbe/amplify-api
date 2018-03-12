package com.amplify.api.aggregates.queue.daos

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

class CommandDaoImpl @Inject()(
    val dbConfigProvider: DatabaseConfigProvider) extends CommandDao with CommandsTable {

  import profile.api._

  override def create(queueCommand: CommandDb): DBIO[CommandDb] = insertCommandQuery += queueCommand
}
