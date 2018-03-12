package com.amplify.api.aggregates.queue.daos

import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[CommandDaoImpl])
trait CommandDao {

  def create(queueCommand: CommandDb): DBIO[CommandDb]
}
