package com.amplify.api.domain.models

case class Queue(
    currentPlaylist: Option[Playlist],
    pastItems: List[QueueItem],
    currentItem: Option[QueueItem],
    futureItems: List[QueueItem]) {

  def allItems: List[QueueItem] = pastItems ++ currentItem.toList ++ futureItems
}

object Queue {

  def apply(): Queue = {
    Queue(currentPlaylist = None, pastItems = Nil, currentItem = None, futureItems = Nil)
  }
}
