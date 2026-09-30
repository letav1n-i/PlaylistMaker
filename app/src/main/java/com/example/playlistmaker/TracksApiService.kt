package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksApiService {

    @GET("search")
    fun search(
        @Query("entity") entity: String = "song",
        @Query("term") term: String
    ): Call<SearchTrackResponse>
}