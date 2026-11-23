package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors
import java.util.concurrent.Future

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    private var searchTask: Future<*>? = null

    override fun searchTracks(query: String, consumer: TracksInteractor.TracksConsumer) {
        cancelSearch()
        searchTask = executor.submit {
            val result = repository.searchTracks(query)
            if (!Thread.currentThread().isInterrupted) {
                consumer.consume(result)
            }
        }
    }

    override fun cancelSearch() {
        searchTask?.cancel(true)
        searchTask = null
        repository.cancelSearch()
    }

    override fun release() {
        cancelSearch()
        executor.shutdownNow()
    }
}
