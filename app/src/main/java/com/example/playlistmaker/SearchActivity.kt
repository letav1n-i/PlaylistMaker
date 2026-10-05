package com.example.playlistmaker

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.StateViewTools.Companion.hideKeyboard
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        adapter.tracks = tracks
        tracksList.adapter = adapter

        adapter.onItemClick = { track ->

            if (tracksSearch.any { it.trackId == track.trackId }) {
                tracksSearch.removeAll { it.trackId == track.trackId }
            }

            tracksSearch.add(0, track)

            while (tracksSearch.size > 10) {
                tracksSearch.removeAt(tracksSearch.size - 1)
            }
            searchAdapter.notifyDataSetChanged()

            writeHistorySearch()
        }

        searchAdapter.tracks = tracksSearch
        searchHistoryList.adapter = ConcatAdapter(searchAdapter, buttonAdapter)

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
                hideKeyboard(this)
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
        val query = searchEditText.text.toString()
        if (query.isBlank()) {
            searchEditText.clearFocus()
            showEmpty()
            return
        }
        NetworkClient.trackService.search(term = query)
            .enqueue(object : Callback<SearchTrackResponse> {
                override fun onResponse(
                    call: Call<SearchTrackResponse>,
                    response: Response<SearchTrackResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        val results = response.body()?.results.orEmpty().filterNotNull()
                        if (results.isEmpty()) {
                            showEmpty()
                        } else {
                            showResults()
                            tracks.addAll(results)
                            adapter.notifyDataSetChanged()
                            tracksList.layoutManager?.scrollToPosition(0)
                        }
                    } else {
                        showError()
                    }
                }

                override fun onFailure(call: Call<SearchTrackResponse>, t: Throwable) {
                    showError()
                }
            })
    }

    fun inputSearchTextWatcher() {
        searchEditText.doOnTextChanged { text, start, before, count ->
            clearButton.visibility = StateViewTools.changeVisibility(text)
            inputSearchText = text.toString()

            emptyResultPlaceholder.isGone = true
            errorPlaceholder.isGone = true

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

    private fun writeHistorySearch() {
        val json = Gson().toJson(tracksSearch)

        (applicationContext as App).sharedPrefs.edit {
            putString(SEARCH_RESULT_KEY, json)
        }
    }

    private fun readHistorySearch(): ArrayList<Track> {
        val json = (applicationContext as App).sharedPrefs.getString(SEARCH_RESULT_KEY, "") ?: ""

        if (json.isEmpty()) return ArrayList()

        val array = Gson().fromJson(json, Array<Track>::class.java)
        return array?.toCollection(ArrayList()) ?: ArrayList()
    }

    private fun loadSearchResult() {
        tracksSearch.clear()
        tracksSearch.addAll(readHistorySearch())
        searchAdapter.notifyDataSetChanged()
    }

    private fun clearSearchHistory() {
        tracksSearch.clear()
        writeHistorySearch()
        searchAdapter.notifyDataSetChanged()
        updateHistoryVisibility()
    }

    private fun clearInput() {
        searchEditText.setText("")
        hideKeyboard(this)
        tracks.clear()
        adapter.notifyDataSetChanged()
        tracksList.isGone = true
    }

    private fun initViews() {
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
        tracksList.isVisible = true

        searchResultBlock.isGone = true
        emptyResultPlaceholder.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun showEmpty() {
        emptyResultPlaceholder.isVisible = true

        searchResultBlock.isGone = true
        tracksList.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun showError() {
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

    private companion object {
        const val SEARCH_INPUT_TAG = "SEARCH_INPUT"
        const val SEARCH_INPUT_QUERY = ""
        const val SEARCH_RESULT_KEY = "search_result_list_key"
    }
}