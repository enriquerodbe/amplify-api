package com.amplify.api.it.fixtures

import com.amplify.api.daos.models.DbUser
import com.amplify.api.daos.schema.UsersTable
import com.amplify.api.domain.models.AuthProviderType.Spotify
import com.amplify.api.domain.models.primitives.{Id, Name}

trait DbUserFixture extends BaseDbFixture with CommonData with UsersTable {

  import profile.api._

  val aliceDbUser = DbUser(aliceDbUserId, "Alice", Spotify → aliceSpotifyId)
  val bobDbUser = DbUser(bobDbUserId, "Bob Marley", Spotify → bobSpotifyId)

  def insertUser(user: DbUser): Id = {
    await(db.run(usersTable returning usersTable.map(_.id) += user))
  }
  def findUsers(name: Name): Seq[DbUser] = {
    await(db.run(usersTable.filter(_.name === name).result))
  }

  insertUser(aliceDbUser)
}
