package com.example.playlistmaker

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class StateViewTools {
    companion object {

        fun changeVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }

        fun hideKeyboard(activity: Activity) {
            WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                .hide(WindowInsetsCompat.Type.ime())
        }

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}
