// UIUtils.kt
package com.example.dnplayer

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

object UIUtils {
    fun setGradientStatusBar(activity: AppCompatActivity, gradientResID: Int, rootLayoutID: Int) {
        val gradientDrawable: Drawable = ContextCompat.getDrawable(activity, gradientResID)!!
        activity.window.statusBarColor = android.graphics.Color.TRANSPARENT
        activity.window.navigationBarColor = ContextCompat.getColor(activity, android.R.color.transparent)
        activity.window.decorView.background = gradientDrawable

        val rootLayout = activity.findViewById<ConstraintLayout>(rootLayoutID)
        rootLayout.background = gradientDrawable
    }
}
