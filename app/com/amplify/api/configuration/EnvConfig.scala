package com.amplify.api.configuration

import akka.util.Timeout
import javax.inject.Inject
import play.api.Configuration
import scala.concurrent.duration.DurationDouble

class EnvConfig @Inject()(config: Configuration) {

  val spotifyWebApiUrl = config.get[String]("spotify.url.web_api")
  val spotifyAccountsUrl = config.get[String]("spotify.url.accounts")
  val spotifyRedirectUri = config.get[String]("spotify.redirect_uri")
  val spotifyClientId = config.get[String]("spotify.client.id")
  val spotifyClientSecret = config.get[String]("spotify.client.secret")

  val coinsDefaultMaxUsages = config.get[Int]("coins.default.max_usages")
  val coinsCreateMax = config.get[Int]("coins.create.max_per_request")

  val defaultAskTimeout = Timeout(3.seconds)
}
