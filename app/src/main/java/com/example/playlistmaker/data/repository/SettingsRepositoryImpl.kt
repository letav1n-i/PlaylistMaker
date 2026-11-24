package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val preferences: SharedPreferences) : SettingsRepository {
    override fun isDarkThemeEnabled(): Boolean = preferences.getBoolean(THEME_KEY, false)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        preferences.edit { putBoolean(THEME_KEY, enabled) }
    }

    private companion object {
        const val THEME_KEY = "key_app_theme"
    }
}
