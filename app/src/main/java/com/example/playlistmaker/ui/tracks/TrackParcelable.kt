package com.example.playlistmaker.ui.tracks

import android.os.Parcelable
import com.example.playlistmaker.domain.models.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackParcelable(
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTime: Int?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {
    fun toDomain(): Track = Track(
        trackId,
        trackName,
        artistName,
        trackTime,
        artworkUrl100,
        collectionName,
        releaseDate,
        primaryGenreName,
        country,
        previewUrl
    )
}

fun Track.toParcelable(): TrackParcelable = TrackParcelable(
    trackId,
    trackName,
    artistName,
    trackTime,
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    previewUrl
)
