package com.amplify.api.domain.coin

import com.amplify.api.domain.models.CoinToken
import com.amplify.api.domain.models.primitives.Id

case class DbCoin(id: Id = Id(0L), venueId: Id, token: CoinToken, maxUsages: Int)
