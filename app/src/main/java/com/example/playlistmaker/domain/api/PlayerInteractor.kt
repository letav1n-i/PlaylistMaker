package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerInteractor {
    val state: PlayerState
    fun prepare(url: String, onStateChanged: (PlayerState) -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun release()
}
