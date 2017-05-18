package com.amplify.api.domain.models

sealed trait QueueItemType

object QueueItemType {

  case object Venue extends QueueItemType

  case object User extends QueueItemType
}
