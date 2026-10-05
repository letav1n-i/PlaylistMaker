package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        if (sharedPrefs.getBoolean(APP_THEME_KEY, false)) {
            switchTheme(true)
        } else {
            switchTheme(false)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val APP_PREFERENCES = "playlist_maker_preferences"
        const val APP_THEME_KEY = "key_app_theme"
    }
}