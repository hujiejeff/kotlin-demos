package com.hujiejeff.musicplayer.player

import android.view.View
import androidx.viewpager.widget.ViewPager

class ScaleTransformer: ViewPager.PageTransformer{
    private val MIN_SCALE = 0.8f
    private val MIN_ALPHA = 1f
    override fun transformPage(view: View, position: Float) {
        when {
            position <= -1 || position >= 1 ->{
                view.alpha = 0.8f
                view.scaleX = 0.8f
                view.scaleY = 0.8f
            }

            position <= 0 -> {
                val scale = MIN_SCALE + (1+position) * (1-MIN_SCALE)
                val alpha = MIN_ALPHA + (1+position) * (1-MIN_ALPHA)
                view.scaleX = scale
                view.scaleY = scale
                view.alpha = alpha
            }
            position <= 1 -> {
                val scale = MIN_SCALE + (1-position) * (1-MIN_SCALE)
                val alpha = MIN_ALPHA + (1-position) * (1-MIN_SCALE)
                view.scaleX = scale
                view.scaleY = scale
                view.alpha = alpha
            }
        }
    }
}