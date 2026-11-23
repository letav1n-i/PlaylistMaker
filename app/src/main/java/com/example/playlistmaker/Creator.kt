package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.google.gson.Gson

object Creator {
    fun providePlayerInteractor(): PlayerInteractor = PlayerInteractorImpl(PlayerRepositoryImpl())
    private fun preferences(context: Context) = context.applicationContext
        .getSharedPreferences(App.APP_PREFERENCES, Context.MODE_PRIVATE)

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor =
        SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(preferences(context), Gson()))

    fun provideSettingsInteractor(context: Context): SettingsInteractor =
        SettingsInteractorImpl(SettingsRepositoryImpl(preferences(context)))

    fun provideTracksInteractor(): TracksInteractor {
        val repository = TracksRepositoryImpl(RetrofitNetworkClient())
        return TracksInteractorImpl(repository)
    }
}
