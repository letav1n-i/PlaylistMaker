package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun release()
}
