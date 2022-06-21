package com.example.expandabletextview

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.roundToInt

object DimenUtils {

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    // Used for returning Float for keep correct unit of view. Conflict with method above so I changed difference name
    fun dpToPxFloat(dp: Float): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    fun pxToDp(px: Float): Int {
        return (px / Resources.getSystem().displayMetrics.density).roundToInt()
    }

    fun getScreenWidth(context: Context?): Int {
        if (context == null) return 0
        val windowManager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getScreenHeight(context: Context?): Int {
        if (context == null) return 0
        val windowManager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }
}