package com.amplify.api.it.fixtures

import play.api.db.slick.HasDatabaseConfigProvider
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import slick.jdbc.JdbcProfile

trait BaseDbFixture
  extends HasDatabaseConfigProvider[JdbcProfile]
  with FutureAwaits
  with DefaultAwaitTimeout
