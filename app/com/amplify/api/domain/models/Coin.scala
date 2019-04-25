package com.amplify.api.domain.models

import com.amplify.api.domain.models.primitives.{Code, Uid}

case class Coin(venueUid: Uid, code: Code, remainingUsages: Int)
