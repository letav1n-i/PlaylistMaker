package com.example.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    var trackId: Long,
    var trackName: String?,
    var artistName: String?,
    @SerializedName(value = "trackTimeMillis", alternate = ["trackTime"])
    var trackTime: Int?,
    var artworkUrl100: String?,
    var collectionName: String?,
    var releaseDate: String?,
    var primaryGenreName: String?,
    var country: String?,
    var previewUrl: String?
)
