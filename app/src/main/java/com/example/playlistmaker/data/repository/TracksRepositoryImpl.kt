package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.SearchResult

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun cancelSearch() = networkClient.cancelRequests()

    override fun searchTracks(query: String): SearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(query))

        if (response.resultCode != 200 || response !is TrackSearchResponse) {
            return SearchResult.Error
        }

        val tracks = response.results.orEmpty().filterNotNull().map { it.toDomain() }

        return SearchResult.Success(tracks)
    }
}
