package com.example.playlistmaker

import android.os.Bundle
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

        backBtn.setOnClickListener {
            closeScreen()
        }
    }

    private fun initViews() {
        backBtn = findViewById(R.id.back_btn_toolbar)

        coverTrack = findViewById(R.id.trackCover)
        trackName = findViewById(R.id.trackName)
        trackArtist = findViewById(R.id.trackArtist)
        trackDuration = findViewById(R.id.durationTrack)

        trackAlbum = findViewById(R.id.albumTrack)
        albumGroup = findViewById(R.id.albumGroup)

        trackReleaseYear = findViewById(R.id.releaseYearTrack)
        releaseYearGroup = findViewById(R.id.releaseYearGroup)

        trackGenre = findViewById(R.id.genreTrack)
        genreGroup = findViewById(R.id.genreGroup)

        trackCountry = findViewById(R.id.countryTrack)
        countryGroup = findViewById(R.id.countryGroup)
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
}
