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
        //TODO 懒加载 ok
        //TODO 状态栏 ok
        //TODO 播放页面viewpager ok
        //TODO 音频焦点 ok
        //TODO 耳机事件 ok
        //TODO 权限问题
        //TODO 线程池集成 ok
        //TODO 当没有音乐的时候



        //TODO 1、BUG 数组越界判断，无歌曲，错误的下标
        //TODO 2、
    }
}