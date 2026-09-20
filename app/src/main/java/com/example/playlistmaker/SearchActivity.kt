package com.example.playlistmaker

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.StateViewTools.Companion.hideKeyboard
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
    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        adapter.tracks = tracks
        tracksList.adapter = adapter

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
                hideKeyboard(this, searchEditText)
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
        NetworkClient.trackService.search(term = searchEditText.text.toString())
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
        }
    }

    private fun clearInput() {
        searchEditText.setText("")
        hideKeyboard(this, searchEditText)
        showResults()
        tracks.clear()
        adapter.notifyDataSetChanged()
    }

    private fun initViews() {
        backBtnToolbar = findViewById(R.id.back_btn_toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_btn)
        tracksList = findViewById(R.id.tracksList)

        emptyResultPlaceholder = findViewById(R.id.emptyResultPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        refreshSearchBtn = findViewById(R.id.refreshSearchButton)
    }

    private fun showResults() {
        tracksList.isVisible = true

        emptyResultPlaceholder.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun showEmpty() {
        emptyResultPlaceholder.isVisible = true

        tracksList.isGone = true
        errorPlaceholder.isGone = true
    }

    private fun showError() {
        errorPlaceholder.isVisible = true

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
    }
}