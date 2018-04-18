package com.amplify.api.configuration

import akka.util.Timeout
import javax.inject.Inject
import play.api.Configuration
import scala.concurrent.duration.DurationDouble

class EnvConfig @Inject()(config: Configuration) {

  val spotifyUrl = config.get[String]("spotify.web_api.url")

  val coinsDefaultMaxUsages = config.get[Int]("coins.defaults.max_usages")

  val defaultAskTimeout = Timeout(3.seconds)
}
