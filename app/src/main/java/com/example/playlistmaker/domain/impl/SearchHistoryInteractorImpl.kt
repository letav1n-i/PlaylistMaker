package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> = repository.getHistory()

    override fun addTrack(track: Track): List<Track> {
        val history = repository.getHistory().filterNot { it.trackId == track.trackId }
        val updated = (listOf(track) + history).take(MAX_HISTORY_SIZE)
        repository.saveHistory(updated)
        return updated
    }

    override fun clearHistory() = repository.saveHistory(emptyList())

    private companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}
