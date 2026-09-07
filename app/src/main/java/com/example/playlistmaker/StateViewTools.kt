package com.example.playlistmaker

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager

class StateViewTools {
    companion object {

        fun changeVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }

        fun hideKeyboard(activity: Activity, view: View) {
            val imm = activity
                .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}