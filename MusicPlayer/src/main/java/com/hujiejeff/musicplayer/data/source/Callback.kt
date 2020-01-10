package com.hujiejeff.musicplayer.data.source

/**
 * Create by hujie on 2020/1/10
 */

interface Callback<T> {
    fun onLoaded(t: T)
    fun onFailed(mes: String)
}