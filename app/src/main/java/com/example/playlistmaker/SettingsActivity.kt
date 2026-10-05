package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.content.edit
import com.example.playlistmaker.App.Companion.APP_THEME_KEY

class SettingsActivity : AppCompatActivity() {

    lateinit var backBtnToolbar: ImageButton
    lateinit var shareAppBtn: TextView
    lateinit var supportBtn: TextView
    lateinit var userAgreementBtn: TextView
    lateinit var themeSwitcher: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setThemeSwitcher((applicationContext as App).sharedPrefs)

        backBtnToolbar.setOnClickListener {
            openMainScreen()
        }

        themeSwitcher.setOnCheckedChangeListener{ switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            (applicationContext as App).sharedPrefs.edit {
                putBoolean(APP_THEME_KEY, checked)
            }
        }

        shareAppBtn.setOnClickListener {
            shareApp()
        }

        supportBtn.setOnClickListener {
            writeSupport()
        }

        userAgreementBtn.setOnClickListener {
            openUserAgreement()
        }

    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.course_url
            )
        )

        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.share)
            )
        )
    }

    private fun writeSupport() {
        val writeIntent = Intent(Intent.ACTION_SENDTO)
        val topic = getString(R.string.topic_support)
        val message = getString(R.string.support_message)

        writeIntent.data = "mailto:".toUri()
        writeIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(getString(R.string.student_url)
            )
        )
        writeIntent.putExtra(Intent.EXTRA_SUBJECT, topic)
        writeIntent.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(writeIntent)
    }

    private fun openUserAgreement() {
        val userAgreementIntent = Intent(
            Intent.ACTION_VIEW,
            getString(R.string.user_agreement_url).toUri()
        )
        startActivity(userAgreementIntent)
    }

    private fun openMainScreen() {
        finish()
    }

    private fun initViews() {
        backBtnToolbar = findViewById(R.id.back_btn_toolbar)
        shareAppBtn = findViewById<TextView>(R.id.shareAppTextView)
        supportBtn = findViewById<TextView>(R.id.writeSupportTextView)
        userAgreementBtn = findViewById<TextView>(R.id.userAgreementTextView)
        themeSwitcher = findViewById(R.id.themeSwitcher)
    }

    private fun setThemeSwitcher(sharedPrefs: SharedPreferences) {
        themeSwitcher.isChecked = sharedPrefs.getBoolean(APP_THEME_KEY, false)
    }
}