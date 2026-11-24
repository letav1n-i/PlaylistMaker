package com.example.playlistmaker.ui.audioplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.StateViewTools
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.ui.tracks.TrackParcelable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

const val TRACK_EXTRA_KEY = "track"

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var backBtn: ImageButton
    private lateinit var coverTrack: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var albumGroup: Group
    private lateinit var trackAlbum: TextView
    private lateinit var releaseYearGroup: Group
    private lateinit var trackReleaseYear: TextView
    private lateinit var genreGroup: Group
    private lateinit var trackGenre: TextView
    private lateinit var countryGroup: Group
    private lateinit var trackCountry: TextView
    private lateinit var trackTime: TextView
    private lateinit var playBtn: ImageButton
    private val playerInteractor = Creator.providePlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        initViews()

        val track = getTrack()
        if (track == null) {
            finish()
            return
        }

        setTrack(track)

        playBtn.isEnabled = false
        val previewUrl = track.previewUrl
        if (!previewUrl.isNullOrBlank()) {
            preparePlayer(previewUrl)
        }

        playBtn.setOnClickListener {
            playbackControl()
        }

        backBtn.setOnClickListener {
            closeScreen()
        }
    }

    private fun initViews() {
        backBtn = findViewById(R.id.back_btn_toolbar)

        coverTrack = findViewById(R.id.trackCover)
        trackName = findViewById(R.id.trackName)
        trackArtist = findViewById(R.id.trackArtist)
        trackTime = findViewById(R.id.trackTime)

        trackDuration = findViewById(R.id.durationTrack)

        trackAlbum = findViewById(R.id.albumTrack)
        albumGroup = findViewById(R.id.albumGroup)

        trackReleaseYear = findViewById(R.id.releaseYearTrack)
        releaseYearGroup = findViewById(R.id.releaseYearGroup)

        trackGenre = findViewById(R.id.genreTrack)
        genreGroup = findViewById(R.id.genreGroup)

        trackCountry = findViewById(R.id.countryTrack)
        countryGroup = findViewById(R.id.countryGroup)

        playBtn = findViewById(R.id.playBtn)
    }

    private fun getTrack(): Track? {
        return IntentCompat.getParcelableExtra(intent, TRACK_EXTRA_KEY, TrackParcelable::class.java)
            ?.toDomain()
    }

    private fun setTrack(track: Track) {
        Glide.with(coverTrack.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_empty_placeholder_237)
            .error(R.drawable.ic_empty_placeholder_237)
            .transform(
                CenterCrop(),
                RoundedCorners(StateViewTools.dpToPx(8f, coverTrack.context))
            )
            .into(coverTrack)

        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.formatTime()

        albumGroup.isVisible = !track.collectionName.isNullOrEmpty()
        trackAlbum.text = track.collectionName

        releaseYearGroup.isVisible = !track.getReleaseYear().isNullOrEmpty()
        trackReleaseYear.text = track.getReleaseYear()

        genreGroup.isVisible = !track.primaryGenreName.isNullOrEmpty()
        trackGenre.text = track.primaryGenreName

        countryGroup.isVisible = !track.country.isNullOrEmpty()
        trackCountry.text = track.country
    }

    private fun closeScreen() {
        finish()
    }

    private fun preparePlayer(url: String) {
        playerInteractor.prepare(url) { state ->
            when (state) {
                PlayerState.PREPARED -> {
                    playBtn.isEnabled = true
                    playBtn.setImageResource(R.drawable.ic_play_100)
                    handler.removeCallbacks(progressUpdater)
                    trackTime.text = getString(R.string.start_progress_track)
                }
                PlayerState.ERROR -> {
                    playBtn.isEnabled = false
                    playBtn.setImageResource(R.drawable.ic_play_100)
                    handler.removeCallbacks(progressUpdater)
                }
                else -> Unit
            }
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        playBtn.setImageResource(R.drawable.ic_pause_100)

        handler.removeCallbacks(progressUpdater)
        handler.post(progressUpdater)
    }

    private fun pausePlayer() {
        playerInteractor.pause()
        playBtn.setImageResource(R.drawable.ic_play_100)

        handler.removeCallbacks(progressUpdater)
    }

    private fun playbackControl() {
        when (playerInteractor.state) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            else -> Unit
        }
    }


    private val progressUpdater = object : Runnable {
        override fun run() {
            if (playerInteractor.state == PlayerState.PLAYING) {
                trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(playerInteractor.getCurrentPosition())

                handler.postDelayed(this, PROGRESS_UPDATE_DELAY_MILLIS)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerInteractor.state == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        playerInteractor.release()
    }

    companion object {
        private const val PROGRESS_UPDATE_DELAY_MILLIS = 200L
    }

}
