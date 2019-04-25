package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.{Code, Uid}

case class DbCoin(venueUid: Uid, code: Code, maxUsages: Int)
