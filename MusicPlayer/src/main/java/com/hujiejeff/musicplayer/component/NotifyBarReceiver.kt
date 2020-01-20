package com.hujiejeff.musicplayer.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.hujiejeff.musicplayer.player.AudioPlayer

/**
 * 通知栏广播接收器
 * Create by hujie on 2019/12/31
 */
class NotifyBarReceiver: BroadcastReceiver() {
    companion object {
        const val ACTION_STATUS_BAR = "com.hujiejeff.receiver.NotifyBarReceiver"
        const val EXTRA = "extra"
        const val EXTRA_PLAY_PAUSE = "extra_play_pause"
        const val EXTRA_NEXT = "extra_next"
    }

    private val player get() = AudioPlayer.INSTANCE
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || TextUtils.isEmpty(intent.action)) {
            return
        }
        when(intent.getStringExtra(EXTRA)) {
            EXTRA_PLAY_PAUSE -> player.playOrPause()
            EXTRA_NEXT -> player.next()
        }
    }
}