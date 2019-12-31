package com.hujiejeff.musicplayer.base

import android.app.Application
import android.content.Context
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.storage.Preference
import com.hujiejeff.musicplayer.util.logD

class App : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        logD("App onCreate")
        appContext = applicationContext
        //TODO 不能在这里请求contentprovider，要权限
        AudioPlayer.INSTANCE.init(appContext)
        Preference.init(this)
    }

    fun todo() {
        //TODO 封面加载缓存问题
        //TODO 懒加载
        //TODO 状态栏
        //TODO 播放页面viewpager
    }
}