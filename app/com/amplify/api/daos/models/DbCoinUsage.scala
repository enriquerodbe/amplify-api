package com.amplify.api.daos.models

import com.amplify.api.domain.models.primitives.Id
import java.time.Instant

class DbCoinUsage(id: Id = Id(0L), coinId: Id, time: Instant)
