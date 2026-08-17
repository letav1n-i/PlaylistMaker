package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var searchBtn: Button
    lateinit var libraryBtn: Button
    lateinit var settingsBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        val searchBtnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                openSearchScreen()
            }
        }
        searchBtn.setOnClickListener(searchBtnClickListener)

        libraryBtn.setOnClickListener {
            openMediaLibraryScreen()
        }

        settingsBtn.setOnClickListener {
            openSettingsScreen()
        }
    }

    fun openSearchScreen() {
        val searchIntent = Intent(
            this,
            SearchActivity::class.java
        )

        startActivity(searchIntent)
    }

    fun openMediaLibraryScreen() {
        val mediaLibraryScreen = Intent(
            this,
            MediaLibraryActivity::class.java
        )

        startActivity(mediaLibraryScreen)
    }

    fun openSettingsScreen() {
        val settingsScreen = Intent(
            this,
            SettingsActivity::class.java
        )

        startActivity(settingsScreen)
    }

    fun initViews() {
        searchBtn = findViewById(R.id.search_btn)
        libraryBtn = findViewById(R.id.library_btn)
        settingsBtn = findViewById(R.id.settings_btn)
    }
}