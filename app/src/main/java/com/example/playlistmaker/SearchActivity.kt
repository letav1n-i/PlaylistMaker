package com.example.playlistmaker

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {
    lateinit var searchEditText: EditText
    lateinit var backBtnToolbar: ImageButton
    lateinit var clearButton: ImageButton
    private var inputSearchText = SEARCH_INPUT_QUERY

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