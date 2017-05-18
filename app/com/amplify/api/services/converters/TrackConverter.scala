package com.amplify.api.services.converters

import com.amplify.api.domain.models.Track
import com.amplify.api.services.external.models.TrackData

object TrackConverter {

  def trackDataToTrack(trackData: TrackData): Track = Track(trackData.name, trackData.identifier)
}
