package com.amplify.api.shared.controllers.dtos

import play.api.libs.json.JsonConfiguration
import play.api.libs.json.JsonNaming.SnakeCase

trait DtosDefinition {

  implicit val config = JsonConfiguration(SnakeCase)
}
