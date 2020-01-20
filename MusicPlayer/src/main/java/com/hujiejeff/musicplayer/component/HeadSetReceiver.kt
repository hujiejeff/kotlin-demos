package com.hujiejeff.musicplayer.component

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.hujiejeff.musicplayer.player.AudioPlayer
import com.hujiejeff.musicplayer.util.logD

class HeadSetReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || TextUtils.isEmpty(intent.action)) {
            return
        }
        when(intent.action) {
            Intent.ACTION_HEADSET_PLUG -> handleNormalHeadSet(intent.extras)
            BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> handleBluetoothHeadSet()
        }
    }

    private fun handleNormalHeadSet(extra: Bundle?) {
        val state = extra?.getInt("state")
        logD("headset's state is" + state.toString())
        if (state == 0) {
            AudioPlayer.INSTANCE.pause()
        }
    }

    private fun handleBluetoothHeadSet() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val state = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)
        if (state == BluetoothProfile.STATE_DISCONNECTED) {
            AudioPlayer.INSTANCE.pause()
        }
    }
}