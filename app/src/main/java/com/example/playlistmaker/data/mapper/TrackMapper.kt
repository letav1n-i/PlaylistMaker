package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

fun TrackDto.toDomain(): Track = Track(
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

fun Track.toDto(): TrackDto = TrackDto(
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
