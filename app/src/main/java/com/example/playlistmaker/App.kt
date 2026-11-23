package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        switchTheme(Creator.provideSettingsInteractor(this).isDarkThemeEnabled())
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
    }
}
