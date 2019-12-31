package com.hujiejeff.musicplayer.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import com.hujiejeff.musicplayer.component.Notifier
import com.hujiejeff.musicplayer.component.StatusBarReceiver

/**
 * Create by hujie on 2019/12/31
 */
class PlayService: Service() {

    inner class PlayBinder: Binder() {
        val service get() = this@PlayService
    }
    private  val statusReceiver by lazy { StatusBarReceiver() }

    override fun onBind(p0: Intent?) = PlayBinder()

    override fun onCreate() {
        super.onCreate()
        Notifier.getInstance().init(this)
        val filter = IntentFilter(StatusBarReceiver.ACTION_STATUS_BAR)
        registerReceiver(statusReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(statusReceiver)
    }


}