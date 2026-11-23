package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.SearchResult

interface TracksInteractor {
    fun searchTracks(query: String, consumer: TracksConsumer)
    fun cancelSearch()
    fun release()

    interface TracksConsumer {
        fun consume(result: SearchResult)
    }
}
