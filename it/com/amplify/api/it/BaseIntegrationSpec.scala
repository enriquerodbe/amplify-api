package com.amplify.api.it

import com.amplify.api.services.external.ContentProviderStrategy
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

trait BaseIntegrationSpec extends PlaySpec with GuiceOneAppPerSuite with MockFactory {

  implicit val dbConfig = app.injector.instanceOf[DatabaseConfigProvider]

  val mockContentProvider = mock[ContentProviderStrategy]

  override def fakeApplication(): Application = {
    new GuiceApplicationBuilder().overrides(
      bind[ContentProviderStrategy].toInstance(mockContentProvider))
      .build()
  }
}
