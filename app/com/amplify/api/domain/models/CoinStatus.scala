package com.amplify.api.domain.models

case class CoinStatus(coin: Coin, venue: Venue, currentTrack: Option[QueueItem])
