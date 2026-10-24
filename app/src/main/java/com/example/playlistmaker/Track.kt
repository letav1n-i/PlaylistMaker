package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    var trackId: Long,
    var trackName: String?,
    var artistName: String?,
    @SerializedName("trackTimeMillis")
    var trackTime: Int?,
    var artworkUrl100: String?,
    var collectionName: String?,
    var releaseDate: String?,
    var primaryGenreName: String?,
    // The two-letter country code for the store you want to search.
    // The search uses the default store front for the specified country. For example:
    // US. The default is US.
    var country: String?


) {
    fun formatTime(): String {
        return trackTime?.let {
            val totalSeconds = it / 1000
            "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
        } ?: "--:--"
    }

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast(
        '/',
        "512x512bb.jpg"
    )

    fun getReleaseYear() = releaseDate?.take(4)
}