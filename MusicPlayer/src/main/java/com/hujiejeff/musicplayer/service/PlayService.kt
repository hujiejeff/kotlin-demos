package com.hujiejeff.musicplayer.service

import android.app.Service
import android.bluetooth.BluetoothHeadset
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import com.hujiejeff.musicplayer.component.HeadSetReceiver
import com.hujiejeff.musicplayer.component.Notifier
import com.hujiejeff.musicplayer.component.NotifyBarReceiver
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.execute.AppExecutors
import com.hujiejeff.musicplayer.util.getMusicList

/**
 * Create by hujie on 2019/12/31
 */
class PlayService : Service() {

    inner class PlayBinder : Binder() {
        val service get() = this@PlayService
    }

    private val statusReceiver by lazy { NotifyBarReceiver() }
    private val headSetReceiver by lazy { HeadSetReceiver() }
    private val appExecutors by lazy { AppExecutors() }
    private val localMusicList: MutableList<Music> = mutableListOf()

    override fun onBind(p0: Intent?) = PlayBinder()

    override fun onCreate() {
        super.onCreate()
        Notifier.getInstance().init(this)
        //通知栏广播
        val filter = IntentFilter(NotifyBarReceiver.ACTION_STATUS_BAR)
        registerReceiver(statusReceiver, filter)

        //耳机插拔广播
        IntentFilter().apply {
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            registerReceiver(headSetReceiver, this)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        AudioPlayer.INSTANCE.release()
        unregisterReceiver(statusReceiver)
        unregisterReceiver(headSetReceiver)
    }

    fun loadMusicList(isLocal: Boolean) {
        if (isLocal) {
            loadLocalMusicList()
        } else {
            loadRemoteMusicList()
        }
    }

    private fun loadLocalMusicList() {
        appExecutors.diskIO.execute {
            localMusicList.addAll(getMusicList())
//            AudioPlayer.INSTANCE.mMusicList = localMusicList
        }
    }

    private fun loadRemoteMusicList() {

    }


}