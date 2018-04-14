package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.UserDb
import com.amplify.api.daos.schema.UsersTable
import com.amplify.api.domain.models.AuthProviderType.Spotify
import com.amplify.api.domain.models.primitives.{Id, Name}

trait UserDbFixture extends BaseDbFixture with CommonData with UsersTable {

  import profile.api._

  val aliceUserDb = UserDb(aliceUserDbId, "Alice", Spotify → aliceSpotifyId)
  val bobUserDb = UserDb(bobUserDbId, "Bob Marley", Spotify → bobSpotifyId)

  def insertUser(user: UserDb): Id = {
    db.run(usersTable returning usersTable.map(_.id) += user).await()
  }
  def findUsers(name: Name): Seq[UserDb] = {
    db.run(usersTable.filter(_.name === name).result).await()
  }

  insertUser(aliceUserDb)
}
