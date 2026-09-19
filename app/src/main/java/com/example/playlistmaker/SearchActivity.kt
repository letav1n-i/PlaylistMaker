package com.example.playlistmaker

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    lateinit var searchEditText: EditText
    lateinit var backBtnToolbar: ImageButton
    lateinit var clearButton: ImageButton
    lateinit var tracksList: RecyclerView
    private var inputSearchText = SEARCH_INPUT_QUERY

    val tracks = arrayListOf(
        Track(
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            trackTime = "5:01",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            trackTime = "4:35",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Stayin' Alive",
            artistName = "Bee Gees",
            trackTime = "4:10",
            artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Whole Lotta Love",
            artistName = "Led Zeppelin",
            trackTime = "5:33",
            artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Sweet Child O'Mine",
            artistName = "Guns N' Roses",
            trackTime = "5:03",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()

        backBtnToolbar.setOnClickListener {
            openMainScreen()
        }

        if (savedInstanceState != null) {
            inputSearchText = savedInstanceState.getString(
                SEARCH_INPUT_TAG,
                SEARCH_INPUT_QUERY
            )
            searchEditText.setText(inputSearchText)
        }

        inputSearchTextWatcher()

        clearButton.setOnClickListener {
            clearInput()
        }
    }

    fun inputSearchTextWatcher() {
        searchEditText.doOnTextChanged { text, start, before, count ->
            clearButton.visibility = StateViewTools.changeVisibility(text)
            inputSearchText = text.toString()
        }
    }

    private fun clearInput() {
        searchEditText.setText("")
        StateViewTools.hideKeyboard(this,searchEditText)
    }

    private fun initViews() {
        backBtnToolbar = findViewById(R.id.back_btn_toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_btn)

        tracksList = findViewById(R.id.tracksList)
        tracksList.adapter = TrackAdapter(tracks)
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