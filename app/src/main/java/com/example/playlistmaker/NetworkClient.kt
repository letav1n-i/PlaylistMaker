package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private const val TRACK_BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(TRACK_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val trackService: TracksApiService = retrofit.create(TracksApiService::class.java)
}