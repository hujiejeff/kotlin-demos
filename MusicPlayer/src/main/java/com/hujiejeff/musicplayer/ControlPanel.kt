package com.hujiejeff.musicplayer

import android.view.View
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.util.loadCover
import kotlinx.android.synthetic.main.include_play_bar.view.*

class ControlPanel(val view: View) : View.OnClickListener, OnPlayerEventListener {

    init {
        view.apply {
            iv_play_bar_play.setOnClickListener(this@ControlPanel)
            iv_play_bar_next.setOnClickListener(this@ControlPanel)
        }
    }

    fun loadMusic() {
        AudioPlayer.INSTANCE.currentMusic?.let { updateBar(it) }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_play_bar_play -> AudioPlayer.INSTANCE.playOrPause()
            R.id.iv_play_bar_next -> AudioPlayer.INSTANCE.next()
        }
    }

    override fun onChange(music: Music) {
        updateBar(music)
    }

    override fun onPlayerStart() {
        view.iv_play_bar_play.isSelected = true
    }

    override fun onPlayerPause() {
        view.iv_play_bar_play.isSelected = false
    }

    override fun onPublish(progress: Int) {
        view.pb_play_bar.progress = progress
    }

    override fun onBufferingUpdate(percent: Int) {
    }

    override fun onModeChange(value: Int) {
    }

    private fun updateBar(music: Music) {
        view.apply {
            tv_play_bar_title.text = music.title
            tv_play_bar_artist.text = music.artist
            iv_play_bar_cover.loadCover(music.albumID)
            pb_play_bar.max = music.duration.toInt()
            pb_play_bar.progress = Preference.play_progress
        }
    }
}