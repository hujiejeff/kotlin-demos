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
        AudioPlayer.INSTANCE.init(appContext)
        Preference.init(this)
    }
}