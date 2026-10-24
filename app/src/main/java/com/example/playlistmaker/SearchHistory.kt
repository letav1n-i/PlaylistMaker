package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_RESULT_KEY = "search_result_list_key"

fun readHistorySearch(sharedPrefs: SharedPreferences): List<Track> {
    val json = sharedPrefs.getString(SEARCH_RESULT_KEY, "") ?: ""

    if (json.isEmpty()) return emptyList()

    val array = Gson().fromJson(json, Array<Track>::class.java)
    return array?.toList() ?: emptyList()
}