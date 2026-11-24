package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest

interface NetworkClient {
    fun doRequest(request: TrackSearchRequest): Response
    fun cancelRequests()
}
