package com.amplify.api.configuration

import akka.util.Timeout
import javax.inject.Inject
import play.api.Configuration
import scala.concurrent.duration.DurationDouble

class EnvConfig @Inject()(configuration: Configuration) {

  private def getString(keyName: String): String = configuration.getString(keyName).get

  val spotifyUrl = getString("spotify.web_api.url")

  val defaultAskTimeout = Timeout(2000.millis)
}
