package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.google.gson.JsonParseException
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {
    private val TRACK_BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(TRACK_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService: TracksApiService = retrofit.create(TracksApiService::class.java)
    private val activeCalls = mutableSetOf<Call<TrackSearchResponse>>()

    override fun doRequest(request: TrackSearchRequest): Response {

        val call = trackService.search(term = request.query)
        synchronized(activeCalls) {
            if (Thread.currentThread().isInterrupted) {
                return Response().apply { resultCode = NETWORK_ERROR }
            }
            activeCalls.add(call)
        }
        return try {
            val response = call.execute()
            val httpCode = response.code()
            val body = response.body() ?: if (httpCode == 200) {
                TrackSearchResponse(resultCount = 0, results = emptyList())
            } else {
                Response()
            }

            body.apply {
                resultCode = httpCode
            }
        } catch (exception: IOException) {
            Response().apply {
                resultCode = NETWORK_ERROR
            }
        } catch (exception: JsonParseException) {
            Response().apply { resultCode = NETWORK_ERROR }
        } finally {
            synchronized(activeCalls) { activeCalls.remove(call) }
        }
    }

    override fun cancelRequests() {
        synchronized(activeCalls) {
            activeCalls.forEach { it.cancel() }
        }
    }

    private companion object {
        const val NETWORK_ERROR = -1
    }
}
