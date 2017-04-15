package com.amplify.api.daos

import com.amplify.api.daos.models.UserDb
import com.google.inject.ImplementedBy
import slick.dbio.DBIO

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def create(user: UserDb): DBIO[UserDb]
}
