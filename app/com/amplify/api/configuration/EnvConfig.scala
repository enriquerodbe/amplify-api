package com.amplify.api.configuration

import akka.util.Timeout
import javax.inject.Inject
import play.api.Configuration
import scala.concurrent.duration.DurationDouble

class EnvConfig @Inject()(config: Configuration) {

  val spotifyUrl = config.get[String]("spotify.web_api.url")

  val coinsDefaultMaxUsages = config.get[Int]("coins.default.max_usages")
  val coinsCreateMax = config.get[Int]("coins.create.max_per_request")

  val defaultAskTimeout = Timeout(3.seconds)
}
