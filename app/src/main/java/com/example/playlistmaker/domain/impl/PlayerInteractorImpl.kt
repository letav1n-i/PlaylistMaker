package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.PlayerState

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override var state = PlayerState.DEFAULT
        private set

    override fun prepare(url: String, onStateChanged: (PlayerState) -> Unit) {
        if (state != PlayerState.DEFAULT) return
        state = PlayerState.PREPARING
        repository.prepare(url, onPrepared = {
            if (state != PlayerState.RELEASED) {
                state = PlayerState.PREPARED
                onStateChanged(state)
            }
        }, onCompletion = {
            if (state != PlayerState.RELEASED) {
                state = PlayerState.PREPARED
                onStateChanged(state)
            }
        }, onError = {
            if (state != PlayerState.RELEASED) {
                state = PlayerState.ERROR
                onStateChanged(state)
            }
        })
    }

    override fun play() {
        if (state == PlayerState.PREPARED || state == PlayerState.PAUSED) {
            repository.play()
            state = PlayerState.PLAYING
        }
    }

    override fun pause() {
        if (state == PlayerState.PLAYING) {
            repository.pause()
            state = PlayerState.PAUSED
        }
    }

    override fun getCurrentPosition(): Int = repository.getCurrentPosition()

    override fun release() {
        if (state != PlayerState.RELEASED) {
            state = PlayerState.RELEASED
            repository.release()
        }
    }
}
