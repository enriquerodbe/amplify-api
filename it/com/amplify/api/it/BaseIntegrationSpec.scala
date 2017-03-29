package com.amplify.api.it

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.db.slick.DatabaseConfigProvider

trait BaseIntegrationSpec extends PlaySpec with GuiceOneAppPerSuite {

  implicit val dbConfig = app.injector.instanceOf[DatabaseConfigProvider]
}
