package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.{Code, Id, Uid}

case class DbCoin(id: Id, venueUid: Uid, code: Code, maxUsages: Int)
