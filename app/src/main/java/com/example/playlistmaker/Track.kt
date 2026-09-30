package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    var trackName: String?,
    var artistName: String?,
    @SerializedName("trackTimeMillis")
    var trackTime: Int?,
    var artworkUrl100: String?

) {
    fun formatTime() : String {
        return trackTime?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        } ?: "--:--"
    }
}