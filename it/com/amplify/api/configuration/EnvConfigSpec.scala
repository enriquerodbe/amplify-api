package com.amplify.api.configuration

import com.amplify.api.it.BaseIntegrationSpec
import play.api.Configuration

class EnvConfigSpec extends BaseIntegrationSpec {

  val subject = new EnvConfig(app.injector.instanceOf[Configuration])

  "EnvConfig" should {
    "retrieve existing key" in {
      subject.spotifyWebApiUrl mustEqual "http://test.xyz"
    }
  }
}
