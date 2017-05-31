package com.amplify.api.domain.models

sealed trait QueueEvent {

  def process(queue: Queue): Queue
}

object QueueEvent {

  case object RemoveVenueTracks extends QueueEvent {

    override def process(queue: Queue): Queue = queue.removeVenueTracks()
  }

  case class AddVenueTrack(track: Track) extends QueueEvent {

    override def process(queue: Queue): Queue = queue.addVenueTrack(track)
  }
}
