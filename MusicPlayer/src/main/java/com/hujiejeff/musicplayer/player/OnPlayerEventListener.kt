package com.hujiejeff.musicplayer.player

import com.hujiejeff.musicplayer.data.entity.Music

interface OnPlayerEventListener {
    /*
    * 切换歌曲
    * */
    fun onChange(music: Music, position: Int)

    /*
    * 播放开始
    * */
    fun onPlayerStart()

    /*
    * 播放暂停
    * */
    fun onPlayerPause()

    /*
    * 进度更新
    * */
    fun onPublish(progress: Int)

    /*
    * 缓冲百分比
    * */
    fun onBufferingUpdate(percent: Int)

    /*
    * 模式改变
    * */
    fun onModeChange(value: Int)

}