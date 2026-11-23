package com.example.playlistmaker

import android.media.MediaPlayer
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
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
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
        return IntentCompat.getParcelableExtra(intent, TRACK_EXTRA_KEY, Track::class.java)
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
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener {
            playBtn.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playBtn.setImageResource(R.drawable.ic_play_100)

            handler.removeCallbacks(progressUpdater)
            trackTime.text = "00:00"
        }
        mediaPlayer.prepareAsync()
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playBtn.setImageResource(R.drawable.ic_pause_100)

        handler.removeCallbacks(progressUpdater)
        handler.post(progressUpdater)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playBtn.setImageResource(R.drawable.ic_play_100)

        handler.removeCallbacks(progressUpdater)
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    private val progressUpdater = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(mediaPlayer.currentPosition)

                handler.postDelayed(this, PROGRESS_UPDATE_DELAY_MILLIS)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        mediaPlayer.release()
    }

    companion object {
        private const val PROGRESS_UPDATE_DELAY_MILLIS = 200L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

}
