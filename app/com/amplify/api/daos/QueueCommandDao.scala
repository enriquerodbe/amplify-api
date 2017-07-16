package com.amplify.api.daos

import com.amplify.api.daos.models.QueueCommandDb
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[QueueCommandDaoImpl])
trait QueueCommandDao {

  def create(queueCommand: QueueCommandDb): DBIO[QueueCommandDb]
}
