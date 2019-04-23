package com.amplify.api.domain.coin

import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

private class DbCoinUsage(id: Id = Id(0L), coinId: Id, time: Instant)
