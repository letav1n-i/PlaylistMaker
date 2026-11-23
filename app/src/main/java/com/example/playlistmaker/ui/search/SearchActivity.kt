package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.ui.audioplayer.AudioPlayerActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.StateViewTools
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.ui.audioplayer.TRACK_EXTRA_KEY
import com.example.playlistmaker.ui.tracks.TrackAdapter
import com.example.playlistmaker.ui.tracks.toParcelable
import com.example.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {
    lateinit var searchEditText: EditText
    lateinit var backBtnToolbar: ImageButton
    lateinit var clearButton: ImageButton
    lateinit var tracksList: RecyclerView
    lateinit var emptyResultPlaceholder: LinearLayout
    lateinit var errorPlaceholder: LinearLayout
    lateinit var refreshSearchBtn: Button
    private var inputSearchText = SEARCH_INPUT_QUERY
    private val tracks = ArrayList<Track>()
    lateinit var searchResultBlock: LinearLayout
    lateinit var searchHistoryList: RecyclerView
    private var tracksSearch = ArrayList<Track>()
    private val adapter = TrackAdapter()
    private val searchAdapter = TrackAdapter()
    private val buttonAdapter = SearchHistoryButtonAdapter { clearSearchHistory() }
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var searchProgressBar: ProgressBar
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val historyInteractor by lazy { Creator.provideSearchHistoryInteractor(applicationContext) }
    private var searchGeneration = 0
    private var lastTrackClickTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        adapter.tracks = tracks
        tracksList.adapter = adapter

        adapter.onItemClick = { track ->
            if (clickDebounce()) {
                val history = historyInteractor.addTrack(track)
                tracksSearch.clear()
                tracksSearch.addAll(history)
                searchAdapter.notifyDataSetChanged()

                openAudioPlayerScreen(track)
            }
        }

        searchAdapter.tracks = tracksSearch
        searchHistoryList.adapter = ConcatAdapter(searchAdapter, buttonAdapter)

        searchAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                openAudioPlayerScreen(track)
            }
        }

        loadSearchResult()

        searchEditText.setOnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }

        backBtnToolbar.setOnClickListener {
            openMainScreen()
        }

        if (savedInstanceState != null) {
            inputSearchText = savedInstanceState.getString(
                SEARCH_INPUT_TAG,
                SEARCH_INPUT_QUERY
            )
            searchEditText.setText(inputSearchText)
            if (inputSearchText.isNotEmpty()) {
                performSearch()
            }
        }

        inputSearchTextWatcher()

        clearButton.setOnClickListener {
            clearInput()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                StateViewTools.hideKeyboard(this)
                performSearch()
                true
            } else {
                false
            }
        }

        refreshSearchBtn.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        handler.removeCallbacks(searchRunnable)
        cancelSearch()
        val query = searchEditText.text.toString()
        if (query.isBlank()) {
            searchEditText.clearFocus()
            showEmpty()
            return
        }
        showLoading()
        val generation = searchGeneration
        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: SearchResult) {
                handler.post {
                    if (generation == searchGeneration && !isFinishing && !isDestroyed) {
                        when (result) {
                            is SearchResult.Success -> {
                                tracks.clear()
                                tracks.addAll(result.tracks)
                                adapter.notifyDataSetChanged()
                                if (tracks.isEmpty()) {
                                    showEmpty()
                                } else {
                                    showResults()
                                    tracksList.layoutManager?.scrollToPosition(0)
                                }
                            }
                            SearchResult.Error -> showError()
                        }
                    }
                }
            }
        })
    }

    fun inputSearchTextWatcher() {
        searchEditText.doOnTextChanged { text, start, before, count ->
            clearButton.visibility = StateViewTools.changeVisibility(text)
            inputSearchText = text.toString()
            handler.removeCallbacks(searchRunnable)
            cancelSearch()

            emptyResultPlaceholder.isGone = true
            errorPlaceholder.isGone = true

            if (inputSearchText.isNotBlank()) {
                searchDebounce()
            }

            updateHistoryVisibility()
        }
    }

    private fun updateHistoryVisibility() {

        val show = searchEditText.hasFocus() &&
            searchEditText.text.isEmpty() &&
            tracksSearch.isNotEmpty()

        if (show) {
            searchResultBlock.isVisible = true
            tracksList.isGone = true
            emptyResultPlaceholder.isGone = true
            errorPlaceholder.isGone = true
        } else {
            searchResultBlock.isGone = true
        }
    }

    private fun loadSearchResult() {
        tracksSearch.clear()
        tracksSearch.addAll(historyInteractor.getHistory())
        searchAdapter.notifyDataSetChanged()
    }

    private fun clearSearchHistory() {
        tracksSearch.clear()
        historyInteractor.clearHistory()
        searchAdapter.notifyDataSetChanged()
        updateHistoryVisibility()
    }

    private fun clearInput() {
        searchEditText.setText("")
        StateViewTools.hideKeyboard(this)
        tracks.clear()
        adapter.notifyDataSetChanged()
        tracksList.isGone = true
    }

    private fun initViews() {
        searchProgressBar = findViewById(R.id.searchProgressBar)
        backBtnToolbar = findViewById(R.id.back_btn_toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_btn)
        tracksList = findViewById(R.id.tracksList)

        emptyResultPlaceholder = findViewById(R.id.emptyResultPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        refreshSearchBtn = findViewById(R.id.refreshSearchButton)

        searchResultBlock = findViewById(R.id.searchHistory)
        searchHistoryList = findViewById(R.id.searchHistoryList)
    }

    private fun showResults() {
        searchProgressBar.isGone = true
        tracksList.isVisible = true

        searchResultBlock.isGone = true
        emptyResultPlaceholder.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun showEmpty() {
        searchProgressBar.isGone = true
        emptyResultPlaceholder.isVisible = true

        searchResultBlock.isGone = true
        tracksList.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun showError() {
        searchProgressBar.isGone = true
        errorPlaceholder.isVisible = true

        searchResultBlock.isGone = true
        tracksList.isGone = true
        emptyResultPlaceholder.isGone = true
    }

    private fun openMainScreen() {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT_TAG, inputSearchText)
    }

    private fun openAudioPlayerScreen(track: Track) {
        val audioPlayerScreen = Intent(
            this,
            AudioPlayerActivity::class.java
        )
        audioPlayerScreen.putExtra(TRACK_EXTRA_KEY, track.toParcelable())

        startActivity(audioPlayerScreen)
    }

    private val searchRunnable = Runnable { performSearch() }

    private fun showLoading() {
        searchProgressBar.isVisible = true
        tracksList.isGone = true
        searchResultBlock.isGone = true
        emptyResultPlaceholder.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun cancelSearch() {
        searchGeneration++
        tracksInteractor.cancelSearch()
        searchProgressBar.isGone = true
    }

    override fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
        cancelSearch()
        tracksInteractor.release()
        super.onDestroy()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val currentTime = SystemClock.elapsedRealtime()
        val previousClickTime = lastTrackClickTime
        if (previousClickTime != null &&
            currentTime - previousClickTime < CLICK_DEBOUNCE_DELAY
        ) {
            return false
        }
        lastTrackClickTime = currentTime
        return true
    }

    private companion object {
        private const val SEARCH_INPUT_TAG = "SEARCH_INPUT"
        private const val SEARCH_INPUT_QUERY = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
