package com.hujiejeff.musicplayer.util

import android.os.Build
import android.util.Log
const val TAG = "Music"

/**
* Log
* */
fun logD(mes: String) {
    Log.d(TAG, mes)
}


/**
* 检查版本操作
* */
fun <T> checkAndroidVersionAction(
    version: Int,
    action: () -> T,
    compatAction: (() -> T)? = null
): T? {
    return if (Build.VERSION.SDK_INT >= version) {
        action()
    } else {
        compatAction?.invoke()
    }
}