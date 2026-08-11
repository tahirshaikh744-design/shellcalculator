package com.tahir.scical

import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object AnimationHelper {

    fun applySmoothClick(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(80)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
                }
            }
            false
        }
    }
}
