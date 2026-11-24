package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository
import java.io.IOException

class PlayerRepositoryImpl : PlayerRepository {
    private val mediaPlayer = MediaPlayer()

    override fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            onError()
            true
        }
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        } catch (exception: IOException) {
            onError()
        } catch (exception: IllegalArgumentException) {
            onError()
        } catch (exception: IllegalStateException) {
            onError()
        } catch (exception: SecurityException) {
            onError()
        }
    }

    override fun play() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun release() = mediaPlayer.release()
}
