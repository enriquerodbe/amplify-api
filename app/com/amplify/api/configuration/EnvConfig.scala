package com.amplify.api.configuration

import javax.inject.Inject
import play.api.Configuration

class EnvConfig @Inject()(configuration: Configuration) {

  def getString(keyName: String): String = {
    configuration.getString(keyName).getOrElse {
      throw new IllegalStateException(s"Undefined env config for $keyName")
    }
  }
}
