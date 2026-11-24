package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.data.mapper.toDto
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.JsonParseException

class SearchHistoryRepositoryImpl(
    private val preferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {
    override fun getHistory(): List<Track> {
        val json = preferences.getString(SEARCH_RESULT_KEY, null) ?: return emptyList()
        return try {
            gson.fromJson(json, Array<TrackDto?>::class.java)
                ?.filterNotNull()?.map { it.toDomain() }.orEmpty()
        } catch (exception: JsonParseException) {
            emptyList()
        }
    }

    override fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks.map { it.toDto() })
        preferences.edit { putString(SEARCH_RESULT_KEY, json) }
    }

    private companion object {
        const val SEARCH_RESULT_KEY = "search_result_list_key"
    }
}
