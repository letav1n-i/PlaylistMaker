package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    lateinit var backBtnToolbar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()

        backBtnToolbar.setOnClickListener {
            openMainScreen()
        }
    }

    fun openMainScreen() {
        finish()
    }

    fun initViews() {
        backBtnToolbar = findViewById(R.id.back_btn_toolbar)
    }
}