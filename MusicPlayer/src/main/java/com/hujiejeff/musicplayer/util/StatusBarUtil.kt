package com.hujiejeff.musicplayer.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi

const val VERSION_O = Build.VERSION_CODES.O
const val VERSION_M = Build.VERSION_CODES.M


/**
 * 透明status bar
 * @param isDark 文字颜色变化 dark表示黑色文字
 * */
@RequiresApi(Build.VERSION_CODES.M)
fun Window.setTransparentStatusBar(isDark: Boolean) {
    var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    if (isDark) option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    decorView.systemUiVisibility = option
    statusBarColor = Color.TRANSPARENT
}

/**
 * 半透明status bar
 * */
fun Window.setTranslucentStatusBar() {

}

/**
 * 沉浸模式
 * */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.setImmersive(immersive: Boolean) {
    val option: Int = if (immersive) {
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    } else {
        (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }
    decorView.systemUiVisibility = option
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.setStatusBarColor(color: Int) {
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    statusBarColor = color
}


@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Window.note() {
    //设置透明状态栏
    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    //取消透明状态栏
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
}