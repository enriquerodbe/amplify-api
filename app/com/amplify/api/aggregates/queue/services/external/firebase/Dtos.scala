package com.amplify.api.aggregates.queue.services.external.firebase

import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{Json, JsonConfiguration, Reads}

object Dtos {

  implicit val config = JsonConfiguration(SnakeCase)

  case class SendNotificationResponse(name: Option[String])

  implicit val sendNotificationResponseReads: Reads[SendNotificationResponse] = {
    Json.reads[SendNotificationResponse]
  }
}
